package com.quartz.monitor.db;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;

import com.quartz.monitor.conf.QuartzConfig;

public class QuartzConfigService extends BasedDaoSupport<QuartzConfig> {
	
	public QuartzConfigService() {
		
	}

	/**
	 * 查询所有的JMX实例
	 */
	public List<QuartzConfig> queryAllQuartzConfigs() {
		
		String sql = "select id, uuid, server_container container, instance_name name, host, port, jmx_username username, jmx_password password from quartz_config";
		try {
            return super.queryList(sql, QuartzConfig.class);
		} catch (SQLException e) {
			log.error("Unable to query all quartz configs", e);
		}
		return Collections.emptyList();
	}
	
	public QuartzConfig queryQuartzConfigsByUUID(String uuid) {
		try {
			String sql = "select id, uuid, server_container container, instance_name name, host, port, jmx_username username, jmx_password password from quartz_config where uuid=?";

            return super.query(sql, QuartzConfig.class, uuid);
		} catch (SQLException e) {
			log.error("Unable to query quartz configs by UUID ======>>>>>>" + uuid, e);
		}
		
		return new QuartzConfig();
	}
	
	public int updateQuartzConfigs(QuartzConfig quartzConfig) {
		if(null == quartzConfig) {
			throw new IllegalArgumentException("Parameter can not be null!");
		}
		
		int result = 0;
		String sql = null;
		Object[] params = null;
		if(null != quartzConfig.getId() && quartzConfig.getId() > 0) {
			sql = "update quartz_config set server_container=?, instance_name=?, host=?, port=?, jmx_username=?, jmx_password=? where id=?";
			
			params = new Object[]{
					quartzConfig.getContainer(),
					quartzConfig.getName(),
					quartzConfig.getHost(),
					quartzConfig.getPort(),
					quartzConfig.getUserName(),
					quartzConfig.getPassword(),
					quartzConfig.getId()
			};
		} else if(StringUtils.isNotBlank(quartzConfig.getUuid())) {
			sql = "update quartz_config set server_container=?, instance_name=?, host=?, port=?, jmx_username=?, jmx_password=? where uuid=?";
			
			params = new Object[]{
					quartzConfig.getContainer(),
					quartzConfig.getName(),
					quartzConfig.getHost(),
					quartzConfig.getPort(),
					quartzConfig.getUserName(),
					quartzConfig.getPassword(),
					quartzConfig.getUuid()
			};
		} else {
			throw new IllegalArgumentException("Unable to get id to update this object, " + quartzConfig);
		}
		
		try {
			result = super.update(sql, params);
		} catch (SQLException e) {
			log.error("Unable to update quartz configs ======>>>>>>" + quartzConfig, e);
		}
		
		return result;
	}
	
	public int saveQuartzConfigs(QuartzConfig quartzConfig) {
		if(null == quartzConfig || !quartzConfig.isValid()) {
			throw new IllegalArgumentException("Parameter can not be null or missing required params!");
		}
		
		String sql = "insert into quartz_config(uuid, server_container, instance_name, host, port, jmx_username, jmx_password) values(?,?,?,?,?,?,?)";
		
		Object[] params = new Object[]{
				quartzConfig.getUuid(),
				quartzConfig.getContainer(),
				quartzConfig.getName(),
				quartzConfig.getHost(),
				quartzConfig.getPort(),
				quartzConfig.getUserName(),
				quartzConfig.getPassword()
		};
		
		try {
			int insertResult = super.update(sql, params);
			return insertResult;
		} catch (SQLException e) {
			log.error("Unable to insert quartz configs ======>>>>>>" + quartzConfig, e);
		}
		
		return 0;
	}
	
	public int deleteById(int id) {
		QueryRunner run = new QueryRunner(DBUtil.getDataSource());
		
		String sql = "delete from quartz_config where id=?";
		try {
			int deleteResult = run.update(sql, id);
			return deleteResult;
		} catch (SQLException e) {
			log.error("Unable to delete quartz configs by ID======>>>>>>" + id, e);
		}
		
		return 0;
	}
	
	public int deleteByUUID(String uuid) {
		if(StringUtils.isBlank(uuid)) {
			throw new IllegalArgumentException("Parameter can not be null or empty!");
		}
		
		String sql = "delete from quartz_config where uuid=?";
		try {
			int deleteResult = super.update(sql, uuid);
			return deleteResult;
		} catch (SQLException e) {
			log.error("Unable to delete quartz configs by UUID======>>>>>>" + uuid, e);
		}
		
		return 0;
	}

	/**
	 * 清空表
	 * @return
	 */
	public int empty() {
		
		String sql = "delete from quartz_config";
		try {
			int deleteResult = super.update(sql);
			return deleteResult;
		} catch (SQLException e) {
			log.error("Unable to delete all quartz configs", e);
		}
		
		return 0;
	}
}
