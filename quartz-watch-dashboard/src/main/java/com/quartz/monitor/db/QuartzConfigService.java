package com.quartz.monitor.db;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.quartz.monitor.conf.QuartzConfig;

@Service
public class QuartzConfigService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// SQL constants to avoid string duplication
	private static final String BASE_SELECT_SQL =
		"SELECT id, uuid, server_container AS container, instance_name AS name, host, port, " +
		"jmx_username AS userName, jmx_password AS password FROM quartz_config";

	private static final String SELECT_BY_UUID_SQL = BASE_SELECT_SQL + " WHERE uuid = :uuid";

	private static final String UPDATE_SQL =
		"UPDATE quartz_config SET server_container = :container, " +
		"instance_name = :name, host = :host, port = :port, " +
		"jmx_username = :userName, jmx_password = :password ";

	private static final String UPDATE_BY_ID_SQL = UPDATE_SQL + "WHERE id = :id";

	private static final String UPDATE_BY_UUID_SQL = UPDATE_SQL + "WHERE uuid = :uuid";

	private static final String INSERT_SQL =
		"INSERT INTO quartz_config(uuid, server_container, instance_name, host, port, jmx_username, jmx_password) " +
		"VALUES(:uuid, :container, :name, :host, :port, :userName, :password)";

	private static final String DELETE_BY_ID_SQL = "DELETE FROM quartz_config WHERE id = :id";

	private static final String DELETE_BY_UUID_SQL = "DELETE FROM quartz_config WHERE uuid = :uuid";

	private static final String DELETE_ALL_SQL = "DELETE FROM quartz_config";

	/**
	 * 查询所有的JMX实例
	 * @return 所有Quartz配置列表
	 */
	public List<QuartzConfig> getAllConfigs() {
		log.debug("Querying all quartz configurations");
		return namedParameterJdbcTemplate.queryForList(BASE_SELECT_SQL, Collections.emptyMap(), QuartzConfig.class);
	}


	/**
	 * 根据UUID查询Quartz配置
	 * @param uuid 配置的UUID
	 * @return 匹配的Quartz配置
	 * @throws IllegalArgumentException 如果UUID为空
	 * @throws EmptyResultDataAccessException 如果没有找到匹配的配置
	 */
	public QuartzConfig getConfigByUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			throw new IllegalArgumentException("UUID cannot be null or empty");
		}

		log.debug("Querying quartz configuration by UUID: {}", uuid);
		MapSqlParameterSource params = new MapSqlParameterSource("uuid", uuid);
		try {
			return namedParameterJdbcTemplate.queryForObject(SELECT_BY_UUID_SQL, params, QuartzConfig.class);
		} catch (EmptyResultDataAccessException e) {
			log.warn("No quartz configuration found for UUID: {}", uuid);
			throw e;
		}
	}

	/**
	 * 根据UUID查询Quartz配置（兼容旧方法）
	 * @param uuid 配置的UUID
	 * @return 匹配的Quartz配置
	 * @throws IllegalArgumentException 如果UUID为空
	 * @throws EmptyResultDataAccessException 如果没有找到匹配的配置
	 * @deprecated 使用 {@link #getConfigByUuid(String)} 代替
	 */
	@Deprecated
	public QuartzConfig queryQuartzConfigsByUUID(String uuid) {
		return getConfigByUuid(uuid);
	}

	/**
	 * 根据ID更新Quartz配置
	 * @param quartzConfig 要更新的配置（必须包含有效的ID）
	 * @return 更新的记录数
	 * @throws IllegalArgumentException 如果配置为空或ID无效
	 */
	public int updateConfigById(QuartzConfig quartzConfig) {
		if (quartzConfig == null) {
			throw new IllegalArgumentException("Parameter cannot be null");
		}

		if (quartzConfig.getId() == null || quartzConfig.getId() <= 0) {
			throw new IllegalArgumentException("Invalid ID in configuration: " + quartzConfig);
		}

		log.debug("Updating quartz configuration by ID: {}", quartzConfig.getId());

		return namedParameterJdbcTemplate.update(UPDATE_BY_ID_SQL, new BeanPropertySqlParameterSource(quartzConfig));
	}

	/**
	 * 根据UUID更新Quartz配置
	 * @param quartzConfig 要更新的配置（必须包含有效的UUID）
	 * @return 更新的记录数
	 * @throws IllegalArgumentException 如果配置为空或UUID无效
	 */
	public int updateConfigByUuid(QuartzConfig quartzConfig) {
		if (quartzConfig == null) {
			throw new IllegalArgumentException("Parameter cannot be null");
		}

		if (StringUtils.isBlank(quartzConfig.getUuid())) {
			throw new IllegalArgumentException("Invalid UUID in configuration: " + quartzConfig);
		}

		log.debug("Updating quartz configuration by UUID: {}", quartzConfig.getUuid());

		return namedParameterJdbcTemplate.update(UPDATE_BY_UUID_SQL, new BeanPropertySqlParameterSource(quartzConfig));
	}

	/**
	 * 更新Quartz配置（兼容旧方法，推荐使用更具体的updateConfigById或updateConfigByUuid方法）
	 * @param quartzConfig 要更新的配置
	 * @return 更新的记录数
	 * @throws IllegalArgumentException 如果配置为空或没有有效的ID或UUID
	 * @deprecated 使用 {@link #updateConfigById(QuartzConfig)} 或 {@link #updateConfigByUuid(QuartzConfig)} 代替
	 */
	@Deprecated
	public int updateQuartzConfigs(QuartzConfig quartzConfig) {
		if (quartzConfig == null) {
			throw new IllegalArgumentException("Parameter cannot be null");
		}

		log.debug("Using deprecated update method for quartz configuration: {}", quartzConfig);

		if (quartzConfig.getId() != null && quartzConfig.getId() > 0) {
			return updateConfigById(quartzConfig);
		} else if (StringUtils.isNotBlank(quartzConfig.getUuid())) {
			return updateConfigByUuid(quartzConfig);
		}

		throw new IllegalArgumentException("Unable to update: neither ID nor UUID is valid in object: " + quartzConfig);
	}

	/**
	 * 保存新的Quartz配置
	 * @param quartzConfig 要保存的配置
	 * @return 插入的记录数
	 * @throws IllegalArgumentException 如果配置为空或缺少必要参数
	 */
	public int saveConfig(QuartzConfig quartzConfig) {
		if (quartzConfig == null || !quartzConfig.isValid()) {
			throw new IllegalArgumentException("Parameter cannot be null or missing required params");
		}

		log.debug("Saving new quartz configuration: {}", quartzConfig);

		return namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(quartzConfig));
	}

	/**
	 * 保存新的Quartz配置（兼容旧方法）
	 * @param quartzConfig 要保存的配置
	 * @return 插入的记录数
	 * @throws IllegalArgumentException 如果配置为空或缺少必要参数
	 * @deprecated 使用 {@link #saveConfig(QuartzConfig)} 代替
	 */
	@Deprecated
	public int saveQuartzConfigs(QuartzConfig quartzConfig) {
		return saveConfig(quartzConfig);
	}

	/**
	 * 根据ID删除Quartz配置
	 * @param id 要删除的配置ID
	 * @return 删除的记录数
	 * @throws IllegalArgumentException 如果ID无效
	 */
	public int deleteConfigById(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("ID must be greater than 0");
		}

		log.debug("Deleting quartz configuration by ID: {}", id);
		MapSqlParameterSource params = new MapSqlParameterSource("id", id);
		return namedParameterJdbcTemplate.update(DELETE_BY_ID_SQL, params);
	}

	/**
	 * 根据ID删除Quartz配置（兼容旧方法）
	 * @param id 要删除的配置ID
	 * @return 删除的记录数
	 * @throws IllegalArgumentException 如果ID无效
	 * @deprecated 使用 {@link #deleteConfigById(int)} 代替
	 */
	@Deprecated
	public int deleteById(int id) {
		return deleteConfigById(id);
	}

	/**
	 * 根据UUID删除Quartz配置
	 * @param uuid 要删除的配置UUID
	 * @return 删除的记录数
	 * @throws IllegalArgumentException 如果UUID为空
	 */
	public int deleteConfigByUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			throw new IllegalArgumentException("UUID cannot be null or empty");
		}

		log.debug("Deleting quartz configuration by UUID: {}", uuid);
		MapSqlParameterSource params = new MapSqlParameterSource("uuid", uuid);
		return namedParameterJdbcTemplate.update(DELETE_BY_UUID_SQL, params);
	}

	/**
	 * 根据UUID删除Quartz配置（兼容旧方法）
	 * @param uuid 要删除的配置UUID
	 * @return 删除的记录数
	 * @throws IllegalArgumentException 如果UUID为空
	 * @deprecated 使用 {@link #deleteConfigByUuid(String)} 代替
	 */
	@Deprecated
	public int deleteByUUID(String uuid) {
		return deleteConfigByUuid(uuid);
	}

	/**
	 * 清空表
	 * @return 删除的记录数
	 */
	public int deleteAllConfigs() {
		log.debug("Emptying quartz configuration table");
		return namedParameterJdbcTemplate.update(DELETE_ALL_SQL, Collections.emptyMap());
	}

	/**
	 * 清空表（兼容旧方法）
	 * @return 删除的记录数
	 * @deprecated 使用 {@link #deleteAllConfigs()} 代替
	 */
	@Deprecated
	public int empty() {
		return deleteAllConfigs();
	}
}
