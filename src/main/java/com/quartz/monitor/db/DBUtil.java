package com.quartz.monitor.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DBUtil {

	private static final Logger log = LoggerFactory.getLogger(DBUtil.class);

	private static DataSource dataSource = null;

	static {
		dataSource = DataSourceInit.getBasicDataSource();
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			log.error("Unable to get connection from data source!", e);
		}
		return conn;
	}

	public static void setDataSource(DataSource dataSource) {
		if (null == dataSource) {
			DBUtil.dataSource = DataSourceInit.getBasicDataSource();
		} else {
			DBUtil.dataSource = dataSource;
		}
	}

	public static void closeConn(Connection conn, PreparedStatement ps,
			ResultSet rs) throws SQLException {
		
		if (rs != null) {
			rs.close();
			rs = null;
		}
			
		if (ps != null) {
			ps.close();
			ps = null;
		}
		
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}
	
}
