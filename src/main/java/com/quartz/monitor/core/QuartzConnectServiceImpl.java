package com.quartz.monitor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.object.QuartzInstance;
import com.quartz.monitor.object.Scheduler;
import com.quartz.monitor.util.JMXUtil;

/**
 * 处理应用与Quartz的连接（使用JMX）
 * 
 * @author guolei
 * 
 */
public class QuartzConnectServiceImpl implements QuartzConnectService {
	
	private static final Logger log = LoggerFactory.getLogger(QuartzConnectServiceImpl.class);

	@Override
	public QuartzInstance initInstance(QuartzConfig config) throws Exception {
		
		JMXServiceURL jmxServiceURL = JMXUtil.createQuartzInstanceConnection(config);
		
		JMXConnector connector = JMXConnectorFactory.connect(jmxServiceURL, this.getCredentialEnvironment(config));
		MBeanServerConnection connection = connector.getMBeanServerConnection();

		ObjectName mBName = new ObjectName("quartz:type=QuartzScheduler,*");
		Set<ObjectName> names = connection.queryNames(mBName, null);
		QuartzInstance quartzInstance = new QuartzInstance();
		quartzInstance.setMBeanServerConnection(connection);
		quartzInstance.setJmxConnector(connector);
		quartzInstance.setUuid(config.getUuid());

		List<Scheduler> schList = new ArrayList<Scheduler>();
		for (ObjectName objectName : names) // for each scheduler.
		{
			QuartzJMXAdapter jmxAdapter = QuartzJMXAdapterFactory
					.initQuartzJMXAdapter(objectName, connection);
			quartzInstance.setJmxAdapter(jmxAdapter);

			Scheduler scheduler = jmxAdapter.getSchedulerByJmx(quartzInstance,
					objectName);
			schList.add(scheduler);

			// attach listener
			// connection.addNotificationListener(objectName, listener, null,
			// null);
			log.info("added listener " + objectName.getCanonicalName());
			// QuartzInstance.putListener(listener);
		}
		quartzInstance.setSchedulerList(schList);
		return quartzInstance;
	}

	@Override
	public void shutdown(QuartzInstance quartzInstance) {
		try {
			if (quartzInstance != null) {
				JMXConnector connector = quartzInstance.getJmxConnector();
				if (connector != null) {
					connector.close();
				}
			}
		} catch (Exception ex) {
			log.error("Failed to shutdown jmx connector!", ex);
		}
	}
	
	private Map<String, Object> getCredentialEnvironment(QuartzConfig config) {
		Map<String, Object> env = new HashMap<String, Object>();
		
		env.put(JMXConnector.CREDENTIALS, new String[] { config.getUserName(), config.getPassword() });
		
		return env;
	}
}
