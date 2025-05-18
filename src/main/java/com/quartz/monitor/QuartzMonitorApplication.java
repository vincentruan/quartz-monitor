package com.quartz.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QuartzMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzMonitorApplication.class, args);
    }
} 