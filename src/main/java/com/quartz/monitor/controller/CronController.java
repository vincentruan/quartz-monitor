package com.quartz.monitor.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.monitor.core.CronExpressionParser;
import com.quartz.monitor.util.DateFormateUtil;

@RestController
@RequestMapping("/api/json")
public class CronController {

    private static final Logger log = LoggerFactory.getLogger(CronController.class);
    
    private static final String SINGLE_SPACE = " ";
    private static final String PER_TYPE = "0";
    private static final String ASSIGN_TYPE = "1";
    
    /**
     * 解析Cron表达式
     */
    @PostMapping("/parseCronExp")
    public ResponseEntity<Map<String, Object>> parseCronExp(@RequestBody Map<String, String> requestMap) {
        Map<String, Object> response = new HashMap<>();
        
        String cronExpression = requestMap.get("cronExpression");
        log.debug("Cron Expression is ========>>>>>{}", cronExpression);
        
        if (StringUtils.isBlank(cronExpression)) {
            response.put("code", 400);
            response.put("message", "Cron Expression不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            CronExpressionParser exp = new CronExpressionParser(cronExpression.trim());
            
            Date dd = new Date();
            
            // 拆分表达式
            String secLabel = exp.getSecondsField();
            String minLabel = exp.getMinutesField();
            String hhLabel = exp.getHoursField();
            String dayLabel = exp.getDaysOfMonthField();
            String monthLabel = exp.getMonthsField();
            String weekLabel = exp.getDaysOfWeekField();
            String yearLabel = exp.getYearField();
            
            if (null == exp.getTimeAfter(dd)) {
                // 配置的表达式在当前时间之前，这类调度任务永远不会运行
                log.warn("Cron Expression [{}] are set the trigger before current date, it will never be trigger!", cronExpression);
                
                TreeSet<Integer> yearSet = exp.getYearSet();
                // 获取最小的年份
                int year = yearSet.first();
                // 获取当年第一天
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.YEAR, year);
                dd = calendar.getTime();
            }
            
            String startDate = DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd);
            
            List<String> schedulerNextResults = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                try {
                    dd = exp.getNextValidTimeAfter(dd);
                    
                    schedulerNextResults.add(DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd));
                    
                    dd = new Date(dd.getTime() + 1000);
                } catch (Exception e) {
                    schedulerNextResults.add("");
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("secLabel", secLabel);
            data.put("minLabel", minLabel);
            data.put("hhLabel", hhLabel);
            data.put("dayLabel", dayLabel);
            data.put("monthLabel", monthLabel);
            data.put("weekLabel", weekLabel);
            data.put("yearLabel", yearLabel);
            data.put("startDate", startDate);
            data.put("schedulerNextResults", schedulerNextResults);
            
            // 还可以添加分钟、小时、日、月、周、年的具体值
            
            response.put("code", 200);
            response.put("data", data);
            return ResponseEntity.ok(response);
            
        } catch (ParseException e) {
            log.error("解析Cron表达式失败", e);
            response.put("code", 500);
            response.put("message", "解析Cron表达式失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 生成Cron表达式
     */
    @PostMapping("/generateCronExp")
    public ResponseEntity<Map<String, Object>> generateCronExp(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String secLabel = "0";
            String minLabel, hhLabel, dayLabel, weekLabel, monthLabel, yearLabel;
            
            // 分钟表达式
            String minuteexptype = String.valueOf(requestMap.get("minuteexptype"));
            if (StringUtils.equals(PER_TYPE, minuteexptype)) {
                Integer startMinute = Integer.valueOf(String.valueOf(requestMap.get("startMinute")));
                Integer everyMinute = Integer.valueOf(String.valueOf(requestMap.get("everyMinute")));
                minLabel = startMinute + "/" + everyMinute;
            } else {
                @SuppressWarnings("unchecked")
                List<Integer> assignMins = (List<Integer>) requestMap.get("assignMins");
                if (CollectionUtils.isEmpty(assignMins)) {
                    response.put("code", 400);
                    response.put("message", "分钟必须指定!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                
                StringBuilder buf = new StringBuilder();
                for (int key : assignMins) {
                    buf.append(key).append(",");
                }
                
                minLabel = buf.substring(0, buf.length() - 1);
            }
            
            // 设置小时
            String hourexptype = String.valueOf(requestMap.get("hourexptype"));
            if (StringUtils.equals(PER_TYPE, hourexptype)) {
                hhLabel = "*";
            } else {
                @SuppressWarnings("unchecked")
                List<Integer> assignHours = (List<Integer>) requestMap.get("assignHours");
                if (CollectionUtils.isEmpty(assignHours)) {
                    response.put("code", 400);
                    response.put("message", "小时必须指定!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                
                StringBuilder buf = new StringBuilder();
                for (int key : assignHours) {
                    buf.append(key).append(",");
                }
                hhLabel = buf.substring(0, buf.length() - 1);
            }
            
            // 设置礼拜、天
            String useweekck = String.valueOf(requestMap.get("useweekck"));
            if (StringUtils.equals("1", useweekck)) {
                dayLabel = "?";
                
                String weekexptype = String.valueOf(requestMap.get("weekexptype"));
                if (StringUtils.equals(PER_TYPE, weekexptype)) {
                    weekLabel = "*";
                } else {
                    @SuppressWarnings("unchecked")
                    List<Integer> assignWeeks = (List<Integer>) requestMap.get("assignWeeks");
                    if (CollectionUtils.isEmpty(assignWeeks)) {
                        response.put("code", 400);
                        response.put("message", "星期必须指定!");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                    
                    StringBuilder buf = new StringBuilder();
                    for (int key : assignWeeks) {
                        buf.append(key).append(",");
                    }
                    
                    weekLabel = buf.substring(0, buf.length() - 1);
                }
            } else {
                weekLabel = "?";
                
                String dayexptype = String.valueOf(requestMap.get("dayexptype"));
                if (StringUtils.equals(PER_TYPE, dayexptype)) {
                    dayLabel = "*";
                } else {
                    @SuppressWarnings("unchecked")
                    List<Integer> assignDays = (List<Integer>) requestMap.get("assignDays");
                    if (CollectionUtils.isEmpty(assignDays)) {
                        response.put("code", 400);
                        response.put("message", "月份天数必须指定!");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                    
                    StringBuilder buf = new StringBuilder();
                    for (int key : assignDays) {
                        buf.append(key).append(",");
                    }
                    
                    dayLabel = buf.substring(0, buf.length() - 1);
                }
            }
            
            // 设置月份
            String monthexptype = String.valueOf(requestMap.get("monthexptype"));
            if (StringUtils.equals(PER_TYPE, monthexptype)) {
                monthLabel = "*";
            } else {
                @SuppressWarnings("unchecked")
                List<Integer> assignMonths = (List<Integer>) requestMap.get("assignMonths");
                if (CollectionUtils.isEmpty(assignMonths)) {
                    response.put("code", 400);
                    response.put("message", "月份必须指定!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                
                StringBuilder buf = new StringBuilder();
                for (int key : assignMonths) {
                    buf.append(key).append(",");
                }
                monthLabel = buf.substring(0, buf.length() - 1);
            }
            
            // 设置年份
            boolean useYearCorn = false;
            String useyearck = String.valueOf(requestMap.get("useyearck"));
            String yearexptype = String.valueOf(requestMap.get("yearexptype"));
            String assignYearCron = String.valueOf(requestMap.get("assignYearCron"));
            
            if (StringUtils.equals("1", useyearck) && StringUtils.equals(ASSIGN_TYPE, yearexptype) && StringUtils.isNotBlank(assignYearCron)) {
                useYearCorn = true;
                yearLabel = assignYearCron;
            } else {
                yearLabel = "*";
            }
            
            String cronExp = secLabel + SINGLE_SPACE + minLabel
                    + SINGLE_SPACE + hhLabel + SINGLE_SPACE
                    + dayLabel + SINGLE_SPACE + monthLabel
                    + SINGLE_SPACE + weekLabel
                    + (useYearCorn ? SINGLE_SPACE + yearLabel : "");
            
            CronExpressionParser exp = new CronExpressionParser(cronExp);
            String cronExpression = exp.toString();
            
            log.debug("Generate Cron Expression ====>>>>>{}", cronExpression);
            
            Date dd = new Date();
            String startDate = DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd);
            
            List<String> schedulerNextResults = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                dd = exp.getNextValidTimeAfter(dd);
                
                schedulerNextResults.add(DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd));
                dd = new Date(dd.getTime() + 1000);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("cronExpression", cronExpression);
            data.put("startDate", startDate);
            data.put("schedulerNextResults", schedulerNextResults);
            
            response.put("code", 200);
            response.put("data", data);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("生成Cron表达式失败", e);
            response.put("code", 500);
            response.put("message", "生成Cron表达式失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 