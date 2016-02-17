package com.quartz.monitor.db;

import com.quartz.monitor.util.PropertyReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class DataSourceBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(DataSourceBuilder.class);
	
	private static final String DBCP2_PROPERTIES_FILENAME = "dbcp2.properties";

	private static final String HIKARI_PROPERTIES_FILENAME = "/hikari.properties";

	public static DataSource buildHikariDataSource() {
		//load properties
		HikariConfig config = new HikariConfig(HIKARI_PROPERTIES_FILENAME);
        return new HikariDataSource(config);
	}

	public static DataSource buildBasicDataSource() {
		//load properties
		Properties properties = PropertyReader.getProperties(DataSourceBuilder.class, DBCP2_PROPERTIES_FILENAME);

		//创建个BasicDataSourceFactory对象用于创建连接池对象
		try {
			//创建个BasicDataSourceFactory对象用于创建连接池对象
			return BasicDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create datasource by properties because required keys are missed! Properties read from " + DBCP2_PROPERTIES_FILENAME + " in classpath is [" + ToStringBuilder.reflectionToString(properties), e);
		}


	}
}
