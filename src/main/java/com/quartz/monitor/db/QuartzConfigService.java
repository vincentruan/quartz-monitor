package com.quartz.monitor.db;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.quartz.monitor.conf.QuartzConfig;

@Service
public class QuartzConfigService {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 查询所有Quartz配置
     */
    public List<QuartzConfig> queryAllQuartzConfigs() {
        String sql = "select id, uuid, server_container as container, instance_name as name, host, port, jmx_username as userName, jmx_password as password from quartz_config";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuartzConfig.class));
    }
    
    /**
     * 添加Quartz配置
     */
    public boolean addQuartzConfig(QuartzConfig config) {
        if (config == null) {
            return false;
        }
        
        if (StringUtils.isBlank(config.getUuid())) {
            config.setUuid(UUID.randomUUID().toString());
        }
        
        try {
            String sql = "insert into quartz_config(uuid, server_container, instance_name, host, port, jmx_username, jmx_password) values(?, ?, ?, ?, ?, ?, ?)";
            int rows = jdbcTemplate.update(sql, 
                    config.getUuid(), 
                    config.getContainer(), 
                    config.getName(), 
                    config.getHost(), 
                    config.getPort(), 
                    config.getUserName(), 
                    config.getPassword());
            
            return rows > 0;
        } catch (Exception e) {
            log.error("添加Quartz配置失败", e);
            return false;
        }
    }
    
    /**
     * 更新Quartz配置
     */
    public boolean updateQuartzConfig(QuartzConfig config) {
        if (config == null || StringUtils.isBlank(config.getUuid())) {
            return false;
        }
        
        try {
            String sql = "update quartz_config set server_container=?, instance_name=?, host=?, port=?, jmx_username=?, jmx_password=? where uuid=?";
            int rows = jdbcTemplate.update(sql, 
                    config.getContainer(), 
                    config.getName(), 
                    config.getHost(), 
                    config.getPort(), 
                    config.getUserName(), 
                    config.getPassword(),
                    config.getUuid());
            
            return rows > 0;
        } catch (Exception e) {
            log.error("更新Quartz配置失败", e);
            return false;
        }
    }
    
    /**
     * 删除Quartz配置
     */
    public boolean deleteQuartzConfig(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return false;
        }
        
        try {
            String sql = "delete from quartz_config where uuid=?";
            int rows = jdbcTemplate.update(sql, uuid);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除Quartz配置失败", e);
            return false;
        }
    }
    
    /**
     * 查询Quartz配置
     */
    public QuartzConfig getQuartzConfigByUuid(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return null;
        }
        
        try {
            String sql = "select id, uuid, server_container as container, instance_name as name, host, port, jmx_username as userName, jmx_password as password from quartz_config where uuid=?";
            List<QuartzConfig> configs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuartzConfig.class), uuid);
            return configs.isEmpty() ? null : configs.get(0);
        } catch (Exception e) {
            log.error("查询Quartz配置失败", e);
            return null;
        }
    }
}
