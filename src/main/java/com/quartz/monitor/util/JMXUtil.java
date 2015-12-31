package com.quartz.monitor.util;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.StringUtils;

import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.constants.WeblogicConstant;
import com.quartz.monitor.object.JMXInput;
import com.quartz.monitor.object.QuartzInstance;

public class JMXUtil {
	
	public static final String QUARTZ_RUNTIME_CONTAINER_WEBLOGIC = "weblogic";
	
	public static final String QUARTZ_RUNTIME_CONTAINER_TOMCAT = "tomcat";
	
	private static Properties properties = PropertyReader.getProperties(JMXUtil.class.getClass(), "quartz-container.properties");
	
	public static String[] getAllAuthContainers() {
		Collection<Object> cols = properties.values();
		if(null != cols) {
			String[] containers = new String[cols.size()];
			
			int i = 0;
			for (Object obj : cols) {
				if(obj instanceof String) {
					containers[i++] = (String) obj;
				} else {
					containers[i++] = String.valueOf(obj);
				}
			}
			
			return containers;
		}
		
		return new String[]{""};
	}
	
	public static String get(String key) {
		return properties.getProperty(key);
	}
	
	public static String get(String key, String defaultVal) {
		return properties.getProperty(key, defaultVal);
	}
	
	public static final String[] QUARTZ_RUNTIME_CONTAINER = {QUARTZ_RUNTIME_CONTAINER_WEBLOGIC, QUARTZ_RUNTIME_CONTAINER_TOMCAT};

	public static JMXServiceURL createQuartzInstanceConnection(QuartzConfig quartzConfig)
			throws MalformedURLException {
		
		JMXServiceURL jmxServiceURL = getJMXServiceURL(quartzConfig);
		
		return jmxServiceURL;
	}
	
	public static JMXServiceURL getJMXServiceURL(QuartzConfig quartzConfig) throws MalformedURLException {
		String weblogic = JMXUtil.get("container.runtimeserver.quartz.weblogic", "weblogic");
		String tomcat = JMXUtil.get("container.runtimeserver.quartz.tomcat", "tomcat");
		
		String configContainer = quartzConfig.getContainer();
		if(configContainer.equals(weblogic)) {
			return getWeblogicJMXServiceURL(quartzConfig);
		}
		
		if (configContainer.equals(tomcat)) {
			return getTomcatJMXServiceURL(quartzConfig);
		}
		
		return getWeblogicJMXServiceURL(quartzConfig);
	}
	
	private static JMXServiceURL getTomcatJMXServiceURL(QuartzConfig quartzConfig) throws MalformedURLException {
		
		StringBuffer stringBuffer = new StringBuffer().append("service:jmx:rmi:///jndi/rmi://")
				.append(quartzConfig.getHost()).append(":").append(quartzConfig.getPort())
				.append("/jmxrmi");
		
		return new JMXServiceURL(stringBuffer.toString());
	}
	
	private static JMXServiceURL getWeblogicJMXServiceURL(QuartzConfig quartzConfig) throws MalformedURLException {
		
		return new JMXServiceURL(WeblogicConstant.JMX_QUARTZ_CLIENT_PROTOCAL, quartzConfig.getHost(), quartzConfig.getPort(), WeblogicConstant.JMX_QUARTZ_CLIENT_JNDIROOT + WeblogicConstant.JMX_QUARTZ_CLIENT_MSERVER);
	}

	public static boolean isSupported(String version) {
		return StringKit.isNotEmpty(version) && version.startsWith("2");
	}

	public static Object callJMXAttribute(JMXInput jmxInput) throws Exception {
		QuartzInstance quartzInstance = jmxInput.getQuartzInstanceConnection();
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		return connection.getAttribute(jmxInput.getObjectName(), jmxInput.getOperation());
	}

	public static Object callJMXOperation(JMXInput jmxInput) throws Exception {
		QuartzInstance quartzInstance = jmxInput.getQuartzInstanceConnection();
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		return connection.invoke(jmxInput.getObjectName(), jmxInput.getOperation(),
				jmxInput.getParameters(), jmxInput.getSignature());
	}

	public static Object convertToType(CompositeDataSupport compositeDataSupport, String key) {
		if (compositeDataSupport.getCompositeType().getType(key).getClassName()
				.equals("java.lang.String")) {
			return StringUtils.trimToEmpty((String) compositeDataSupport.get(key));
		} else if (compositeDataSupport.getCompositeType().getType(key).getClassName()
				.equals("java.lang.Boolean")) {
			return compositeDataSupport.get(key);
		} else if (compositeDataSupport.getCompositeType().getType(key).getClassName()
				.equals("java.util.Date")) {
			return compositeDataSupport.get(key);
		} else if (compositeDataSupport.getCompositeType().getType(key).getClassName()
				.equals("java.lang.Integer")) {
			return compositeDataSupport.get(key);
		} else if (compositeDataSupport.getCompositeType().getType(key).getClassName()
				.equals("java.lang.Long")) {
			return compositeDataSupport.get(key);
		}
		return new Object();
	}
}
