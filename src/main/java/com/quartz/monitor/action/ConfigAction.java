package com.quartz.monitor.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.core.QuartzConnectService;
import com.quartz.monitor.core.QuartzConnectServiceImpl;
import com.quartz.monitor.core.QuartzInstanceContainer;
import com.quartz.monitor.db.QuartzConfigService;
import com.quartz.monitor.object.QuartzInstance;
import com.quartz.monitor.object.Result;
import com.quartz.monitor.util.JsonUtil;
import com.quartz.monitor.util.Tools;

public class ConfigAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7754239843635732433L;

	private static Logger log = LoggerFactory.getLogger(ConfigAction.class);

	private String uuid;
	private String container;
	private String host;
	private int port;
	private String username;
	private String password;

	private Map<String, QuartzConfig> quartzMap;
	
	private QuartzConfigService quartzConfigService = new QuartzConfigService();

	public String add() throws Exception {

		String id = Tools.generateUUID();
		QuartzConfig quartzConfig = new QuartzConfig(id, this.container, host,
				port, username, password);
		QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
		QuartzInstance quartzInstance = quartzConnectService
				.initInstance(quartzConfig);
		// QuartzInstanceService.putQuartzInstance(quartzInstance);
		QuartzInstanceContainer.addQuartzConfig(quartzConfig);
		QuartzInstanceContainer.addQuartzInstance(id, quartzInstance);
		log.info("add a quartz info!");

		//XstreamUtil.object2XML(quartzConfig);
		quartzConfigService.saveQuartzConfigs(quartzConfig);

		Result result = new Result();
		result.setNavTabId("main");
		result.setMessage("添加成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String list() throws Exception {

		quartzMap = QuartzInstanceContainer.getConfigMap();
		log.info("get quartz map info.map size:" + quartzMap.size());

		return "list";
	}

	public String show() throws Exception {

		QuartzConfig quartzConfig = QuartzInstanceContainer.getQuartzConfig(uuid);
		log.info("get a quartz info! uuid:" + uuid);
		uuid = quartzConfig.getUuid();
		host = quartzConfig.getHost();
		port = quartzConfig.getPort();
		username = quartzConfig.getUserName();
		password = quartzConfig.getPassword();
		return "show";
	}

	public String update() throws Exception {

		QuartzConfig quartzConfig = new QuartzConfig(uuid, this.container,
				host, port, username, password);
		QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
		QuartzInstance quartzInstance = quartzConnectService
				.initInstance(quartzConfig);
		QuartzInstanceContainer.addQuartzConfig(quartzConfig);
		QuartzInstanceContainer.addQuartzInstance(uuid, quartzInstance);
		log.info("update a quartz info!");

		//XstreamUtil.object2XML(quartzConfig);
		quartzConfigService.updateQuartzConfigs(quartzConfig);

		Result result = new Result();
		result.setMessage("修改成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String delete() throws Exception {

		QuartzInstanceContainer.removeQuartzConfig(uuid);
		QuartzInstanceContainer.removeQuartzInstance(uuid);
		log.info("delete a quartz info!");
		//XstreamUtil.removeXml(uuid);
		quartzConfigService.deleteByUUID(uuid);

		Result result = new Result();
		result.setMessage("删除成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, QuartzConfig> getQuartzMap() {
		return quartzMap;
	}

	public void setQuartzMap(Map<String, QuartzConfig> quartzMap) {
		this.quartzMap = quartzMap;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

}
