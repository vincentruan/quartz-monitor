package com.quartz.monitor.conf;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.quartz.monitor.util.JMXUtil;

/**
 * Quartz连接的配置类
 * 
 * @author guolei
 * 
 */
public class QuartzConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7570544458371572333L;
	
	private Integer id;

	private String uuid;
	
	private String container;
	
	private String name;
	
	private String host;
	
	private int port;
	
	private String userName;
	
	private String password;

	public QuartzConfig() {
	}

	public QuartzConfig(String uuid, String container, String host, int port, String userName,
			String password) {
		this.uuid = uuid;
		this.host = host;
		this.container = container;
		this.port = port;
		this.name = this.getName();
		this.userName = userName;
		this.password = password;
	}

	public String getName() {
		this.name = getHost() + "@" + getPort();
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getContainer() {
		if(null == this.container || "".equals(this.container)) {
			return JMXUtil.get("container.runtimeserver.quartz.weblogic", "weblogic");
		}
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "QuartzConfig [id=" + id + ", uuid=" + uuid + ", container="
				+ container + ", name=" + name + ", host=" + host + ", port="
				+ port + ", userName=" + userName + ", password=" + password
				+ "]";
	}

	public boolean isValid() {
		if(StringUtils.isBlank(this.uuid) 
				|| StringUtils.isBlank(this.container)
				|| StringUtils.isBlank(this.host) 
				|| this.port == 0) {
			return false;
		}

		return true;
	}
}
