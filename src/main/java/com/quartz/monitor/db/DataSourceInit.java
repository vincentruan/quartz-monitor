package com.quartz.monitor.db;

import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quartz.monitor.util.PropertyReader;

public class DataSourceInit {
	
	private static final Logger log = LoggerFactory.getLogger(DataSourceInit.class);
	
	private static final String DB_PROPERTIES_FILENAME = "db.properties";
	
	private static final String DEFAULT_DB_USERNAME = "root";
	
	private static final String DEFAULT_DB_PASS = "Oracle123";
	
	private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/oracle";
	
	private static final String DEFAULT_DB_DRIVER = "com.mysql.jdbc.Driver";

	public static BasicDataSource getBasicDataSource() {
		//load properties
		Properties properties = PropertyReader.getProperties(DataSourceInit.class.getClass(), DB_PROPERTIES_FILENAME);
		
		BasicDataSource basicDataSource = null;
		if(null == properties) {
			log.warn("Unable to load {} from classpath! Use default properties to instead.");
			
			String url, username, password, driverName;
			int initialSize = 1, maxIdle = 20, maxWaitMillis = 2000;
			
			url = DEFAULT_DB_URL;
            username = DEFAULT_DB_USERNAME;
            password = DEFAULT_DB_PASS;
            driverName = DEFAULT_DB_DRIVER;
            
            basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(url);  
            basicDataSource.setUsername(username);  
            basicDataSource.setPassword(password);  
            basicDataSource.setDriverClassName(driverName);  
            basicDataSource.setInitialSize(initialSize);  
            basicDataSource.setMaxIdle(maxIdle);  //最大闲置个数  
            basicDataSource.setMaxWaitMillis(maxWaitMillis);//最大等待时间 
            
		} else {

			//创建个BasicDataSourceFactory对象用于创建连接池对象
			try {
				//创建个BasicDataSourceFactory对象用于创建连接池对象
				basicDataSource = BasicDataSourceFactory.createDataSource(properties);
			} catch (Exception e) {
				log.error("found properties, but required keys are missed!", e);
			}
		}
		
        return basicDataSource;
	}
}
