package com.quartzwatch.microbatch;

import com.quartzwatch.api.EnableQuartzWatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QuartzWatch测试应用
 * 
 * @author QuartzWatch
 */
@SpringBootApplication
@EnableQuartzWatch
public class QuartzWatchTestApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(QuartzWatchTestApplication.class, args);
    }
} 