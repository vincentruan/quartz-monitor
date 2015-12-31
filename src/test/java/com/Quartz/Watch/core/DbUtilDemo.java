package com.Quartz.Watch.core;

import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.db.DBUtil;
import com.quartz.monitor.db.QuartzConfigService;
import com.quartz.monitor.util.Tools;

public class DbUtilDemo {
	private QueryRunner qu = new QueryRunner(DBUtil.getDataSource());

	@Test
	public void add() throws Exception {
		String sql = "insert into quartz_config(uuid, server_container, instance_name, host, port, jmx_username, jmx_password) values(?,?,?,?,?,?,?)";
		qu.update(sql, Tools.generateUUID(), "weblogic", "tomcat", "127.0.0.1", 7001, "oracle", "Oracle123");
	}

	@Test
	public void addBatch() throws Exception {
		String sql = "insert into quartz_config(uuid, server_container, instance_name, host, port, jmx_username, jmx_password) values(?,?,?,?,?,?,?)";
		Object[][] param = new Object[10][];
		for (int i = 0; i < param.length; i++) {
			param[i] = new Object[] { Tools.generateUUID(), "weblogic", "tomcat", "127.0.0.1", 7001, "oracle", "Oracle123" };
		}
		qu.batch(sql, param);
	}

	@Test
	public void query() throws Exception {
		String sql = "select id, uuid, server_container container, instance_name name, host, port, jmx_username username, jmx_password password from quartz_config where id=?";
		QuartzConfig ac = qu.query(sql, new BeanHandler<QuartzConfig>(QuartzConfig.class), 1);
		
		System.out.println(ac);
	}

	@Test
	public void queryAll() throws Exception {
		String sql = "select id, uuid, server_container container, instance_name name, host, port, jmx_username username, jmx_password password from quartz_config";
		List<QuartzConfig> list = qu.query(sql, new BeanListHandler<QuartzConfig>(QuartzConfig.class));
		
		System.out.println(list);
	}

	@Test
	public void test1() throws Exception {
		String sql = "select id, uuid, server_container container, instance_name name, host, port, jmx_username username, jmx_password password from quartz_config where id=1";
		Object[] obj = qu.query(sql, new ArrayHandler());
		for (Object ac : obj)
			System.out.println(ac);
	}

	@Test
	public void test2() throws Exception {
		String sql = "select id, uuid, server_container container, instance_name name, host, port, jmx_username username, jmx_password password from quartz_config";
		List<Object[]> list = (List<Object[]>) qu.query(sql, new ArrayListHandler());
		for (Object[] ac : list)
			for (Object o : ac) {
				System.out.println(o);
			}
	}

	@Test
	public void test3() throws Exception {
		QuartzConfigService quartzConfigService = new QuartzConfigService();
		System.out.println(quartzConfigService.empty());
	}
}
