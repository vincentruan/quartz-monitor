package com.quartz.monitor.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quartz.monitor.core.QuartzInstanceContainer;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/config")
    public String config() {
        return "index";
    }
    
    @GetMapping("/check")
    public String check() {
        return "date_list";
    }
    
    @GetMapping("/api/status")
    @ResponseBody
    public Map<String, Object> status() {
        return Map.of(
            "configs", QuartzInstanceContainer.getAllQuartzConfigs(),
            "instances", QuartzInstanceContainer.getAllQuartzInstance(),
            "status", "running"
        );
    }
} 