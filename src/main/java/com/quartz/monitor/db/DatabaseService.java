package com.quartz.monitor.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseService.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public DatabaseService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        return jdbcTemplate.getDataSource().getConnection();
    }
    
    /**
     * 执行查询并将结果映射到指定的对象类型
     */
    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... args) {
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(clazz), args);
    }
    
    /**
     * 执行查询并将结果映射到单个对象
     */
    public <T> T queryForObject(String sql, Class<T> clazz, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(clazz), args);
        } catch (Exception e) {
            log.error("查询单个对象失败: " + sql, e);
            return null;
        }
    }
    
    /**
     * 执行查询并返回Map列表
     */
    public List<Map<String, Object>> queryForList(String sql, Object... args) {
        return jdbcTemplate.queryForList(sql, args);
    }
    
    /**
     * 执行更新操作
     */
    public int update(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }
    
    /**
     * 批量执行更新操作
     */
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
} 