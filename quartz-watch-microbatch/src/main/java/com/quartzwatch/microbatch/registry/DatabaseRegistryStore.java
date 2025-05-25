package com.quartzwatch.microbatch.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quartzwatch.api.Internal;
import com.quartzwatch.api.registry.NodeInfo;
import com.quartzwatch.api.registry.NodeMetadata;
import com.quartzwatch.api.registry.RegistryStore;
import com.quartzwatch.api.sharding.Shard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于数据库的注册存储实现
 * 
 * @author QuartzWatch
 */
@Slf4j
@Internal
public class DatabaseRegistryStore implements RegistryStore {
    
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public DatabaseRegistryStore(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    @Transactional
    public void registerNode(String nodeId, NodeMetadata metadata) {
        try {
            String metadataJson = objectMapper.writeValueAsString(metadata);
            
            String sql = "INSERT INTO QRTZ_WATCH_NODES (NODE_ID, METADATA, LAST_HEARTBEAT, STATUS) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE METADATA = ?, LAST_HEARTBEAT = ?, STATUS = ?";
            
            long now = System.currentTimeMillis();
            jdbcTemplate.update(sql, nodeId, metadataJson, now, "ACTIVE",
                               metadataJson, now, "ACTIVE");
            
            log.info("Node registered in database: {}", nodeId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize node metadata", e);
        }
    }
    
    @Override
    @Transactional
    public void heartbeat(String nodeId) {
        String sql = "UPDATE QRTZ_WATCH_NODES SET LAST_HEARTBEAT = ? WHERE NODE_ID = ?";
        int updated = jdbcTemplate.update(sql, System.currentTimeMillis(), nodeId);
        
        if (updated == 0) {
            log.warn("Heartbeat failed - node not found: {}", nodeId);
        }
    }
    
    @Override
    @Transactional
    public void unregisterNode(String nodeId) {
        String sql = "UPDATE QRTZ_WATCH_NODES SET STATUS = ? WHERE NODE_ID = ?";
        jdbcTemplate.update(sql, "INACTIVE", nodeId);
        log.info("Node unregistered: {}", nodeId);
    }
    
    @Override
    public List<NodeInfo> getNodesForJob(String jobKey) {
        // 暂时返回所有活跃节点，后续可以根据jobKey进行过滤
        return getActiveNodes();
    }
    
    @Override
    public List<NodeInfo> getActiveNodes() {
        String sql = "SELECT NODE_ID, METADATA, LAST_HEARTBEAT, STATUS FROM QRTZ_WATCH_NODES " +
                    "WHERE STATUS = 'ACTIVE' AND LAST_HEARTBEAT > ?";
        
        // 30秒内有心跳的节点认为是活跃的
        long threshold = System.currentTimeMillis() - 30000;
        
        return jdbcTemplate.query(sql, new Object[]{threshold}, new NodeInfoRowMapper());
    }
    
    @Override
    @Transactional
    public void saveShardAllocation(String jobKey, List<Shard> shards) {
        try {
            // 先删除旧的分配
            String deleteSql = "DELETE FROM QRTZ_WATCH_SHARDS WHERE JOB_KEY = ?";
            jdbcTemplate.update(deleteSql, jobKey);
            
            // 插入新的分配
            String insertSql = "INSERT INTO QRTZ_WATCH_SHARDS (JOB_KEY, NODE_ID, SHARD_DATA) VALUES (?, ?, ?)";
            
            for (Shard shard : shards) {
                String shardJson = objectMapper.writeValueAsString(shard);
                jdbcTemplate.update(insertSql, jobKey, shard.getNodeId(), shardJson);
            }
            
            log.info("Saved shard allocation for job: {}", jobKey);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize shard data", e);
        }
    }
    
    @Override
    public List<Shard> getShardAllocation(String jobKey) {
        String sql = "SELECT SHARD_DATA FROM QRTZ_WATCH_SHARDS WHERE JOB_KEY = ?";
        
        return jdbcTemplate.query(sql, new Object[]{jobKey}, (rs, rowNum) -> {
            try {
                String shardJson = rs.getString("SHARD_DATA");
                return objectMapper.readValue(shardJson, Shard.class);
            } catch (Exception e) {
                log.error("Failed to deserialize shard data", e);
                return null;
            }
        });
    }
    
    private class NodeInfoRowMapper implements RowMapper<NodeInfo> {
        @Override
        public NodeInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                String metadataJson = rs.getString("METADATA");
                NodeMetadata metadata = objectMapper.readValue(metadataJson, NodeMetadata.class);
                
                return NodeInfo.builder()
                        .nodeId(rs.getString("NODE_ID"))
                        .metadata(metadata)
                        .lastHeartbeat(rs.getLong("LAST_HEARTBEAT"))
                        .status(NodeInfo.NodeStatus.valueOf(rs.getString("STATUS")))
                        .assignedShards(new int[0]) // 从分片表中查询
                        .build();
            } catch (Exception e) {
                log.error("Failed to deserialize node metadata", e);
                return null;
            }
        }
    }
} 