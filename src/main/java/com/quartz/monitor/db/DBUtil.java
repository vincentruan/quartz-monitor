package com.quartz.monitor.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DBUtil {

	private static final Logger log = LoggerFactory.getLogger(DBUtil.class);

	private static DataSource dataSource = null;

	static {
		dataSource = DataSourceBuilder.buildBasicDataSource();
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

	public static void setDataSource(DataSource _dataSource) {
        if(null != dataSource) {
            log.warn("Unable to set datasource, because datasource has already had value!");
            return;
        }

		if (null == _dataSource) {
			dataSource = DataSourceBuilder.buildHikariDataSource();
			return;
		}

        dataSource = _dataSource;
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

    public static void close() {
        if(null == dataSource) {
            log.debug("Datasource is null, not need to close.");
            return;
        }

        if(dataSource instanceof Closeable) {
            try {
                ((Closeable) dataSource).close();
            } catch (IOException e) {
                log.error("Failed to close datasource", e);
            }
            return;
        }

        if(dataSource instanceof BasicDataSource) {
            try {
                ((BasicDataSource) dataSource).close();
            } catch (SQLException e) {
                log.error("Failed to close datasource", e);
            }
        }
    }
	
}
