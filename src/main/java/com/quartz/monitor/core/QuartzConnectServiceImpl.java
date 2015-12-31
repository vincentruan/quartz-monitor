package com.quartz.monitor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import org.apache.log4j.Logger;

import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.constants.WeblogicConstant;
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
	static Logger log = Logger.getLogger(QuartzConnectServiceImpl.class);

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
	
	public Map<String, ?> getCredentialEnvironment(QuartzConfig config) {
		
		String weblogic = JMXUtil.get("container.runtimeserver.quartz.weblogic", "weblogic");
		String tomcat = JMXUtil.get("container.runtimeserver.quartz.tomcat", "tomcat");
		
		String configContainer = config.getContainer();
		if(configContainer.equals(weblogic)) {
			return this.getWeblogicCredentialEnvironment(config);
		}
		
		if (configContainer.equals(tomcat)) {
			return this.getTomcatCredentialEnvironment(config);
		}
		
		return this.getWeblogicCredentialEnvironment(config);
	}
	
	/**
	 * 获取tomcat的授权环境
	 * @param userName
	 * @param password
	 * @return
	 */
	private Map<String, String[]> getTomcatCredentialEnvironment(QuartzConfig config) {
		Map<String, String[]> env = new HashMap<String, String[]>();
		
		env.put(JMXConnector.CREDENTIALS, new String[] { config.getUserName(), config.getPassword()});
		
		return env;
	}
	
	private Hashtable<String, String> getWeblogicCredentialEnvironment(QuartzConfig config) {
		Hashtable<String, String> h = new Hashtable<String, String>();
		h.put(Context.SECURITY_PRINCIPAL, config.getUserName());
		h.put(Context.SECURITY_CREDENTIALS, config.getPassword());
		h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, WeblogicConstant.JMX_QUARTZ_CLIENT_PKGS);
		
		return h;
	}
}
