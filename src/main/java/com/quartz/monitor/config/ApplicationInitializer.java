package com.quartz.monitor.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.core.QuartzConnectService;
import com.quartz.monitor.core.QuartzConnectServiceImpl;
import com.quartz.monitor.core.QuartzInstanceContainer;
import com.quartz.monitor.db.QuartzConfigService;
import com.quartz.monitor.object.QuartzInstance;

@Component
public class ApplicationInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ApplicationInitializer.class);
    
    @Autowired
    private QuartzConfigService quartzConfigService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("从数据库加载Quartz配置");
        List<QuartzConfig> quartzConfigs = quartzConfigService.queryAllQuartzConfigs();
        
        QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
        QuartzInstance quartzInstance = null;
        
        for (QuartzConfig quartzConfig : quartzConfigs) {
            QuartzInstanceContainer.addQuartzConfig(quartzConfig);
            
            try {
                quartzInstance = quartzConnectService.initInstance(quartzConfig);
                QuartzInstanceContainer.addQuartzInstance(quartzConfig.getUuid(), quartzInstance);
            } catch (Exception e) {
                log.error("初始化Quartz实例失败", e);
            }
        }
    }
} 