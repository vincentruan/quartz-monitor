package com.quartz.monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Configuration for enabling method-level validation
 */
@Configuration
public class ValidationConfig {
    
    /**
     * Enable method-level validation for @Validated annotated classes
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
} 