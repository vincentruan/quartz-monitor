package com.quartz.monitor.core;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.db.QuartzConfigService;
import com.quartz.monitor.vo.QuartzInstance;

public class InitListener implements ServletContextListener {
	private static Logger log = Logger.getLogger(InitListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		/*
		 * use db instead of xml
		 */
		/*
		log.info("load xml to config container");
		String path = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath()
				+ "quartz-config";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			File[] fileList = file.listFiles();
			log.info("find " + fileList.length + " configs of quartz config!");
			for (int i = 0; i < fileList.length; i++) {
				if (!fileList[i].isDirectory()
						&& fileList[i].getName().startsWith("quartz-config-")) {
					try {
						QuartzConfig config = XstreamUtil
								.xml2Object(fileList[i].getAbsolutePath());
						QuartzInstanceContainer.addQuartzConfig(config);
						QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
						QuartzInstance quartzInstance = quartzConnectService
								.initInstance(config);
						QuartzInstanceContainer.addQuartzInstance(
								config.getUuid(), quartzInstance);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		*/
		log.info("load db info to config container");
		QuartzConfigService quartzConfigService = new QuartzConfigService();
		List<QuartzConfig> quartzConfigs = quartzConfigService.queryAllQuartzConfigs();
		
		QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
		QuartzInstance quartzInstance = null;
		for (QuartzConfig quartzConfig : quartzConfigs) {
			QuartzInstanceContainer.addQuartzConfig(quartzConfig);
			
			try {
				quartzInstance = quartzConnectService.initInstance(quartzConfig);
				QuartzInstanceContainer.addQuartzInstance(quartzConfig.getUuid(), quartzInstance);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}
}
