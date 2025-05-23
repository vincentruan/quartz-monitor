package com.quartz.monitor.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.monitor.core.CronExpressionParser;
import com.quartz.monitor.dto.CronData;
import com.quartz.monitor.dto.CronGenerateRequest;
import com.quartz.monitor.dto.CronParseRequest;
import com.quartz.monitor.dto.CronResponse;
import com.quartz.monitor.util.DateFormateUtil;

@RestController
@RequestMapping("/api/json")
public class CronController {

    private static final Logger log = LoggerFactory.getLogger(CronController.class);
    
    private static final String SINGLE_SPACE = " ";
    private static final String PER_TYPE = "0";
    private static final String ASSIGN_TYPE = "1";
    
    @PostMapping("/parseCronExp")
    public ResponseEntity<CronResponse<CronData>> parseCronExp(@Valid @RequestBody CronParseRequest request) {
        log.debug("Cron Expression is ========>>>>>{}", request.getCronExpression());
        
        try {
            CronExpressionParser exp = new CronExpressionParser(request.getCronExpression().trim());
            Date dd = new Date();
            
            CronData data = new CronData();
            data.setSecLabel(exp.getSecondsField());
            data.setMinLabel(exp.getMinutesField());
            data.setHhLabel(exp.getHoursField());
            data.setDayLabel(exp.getDaysOfMonthField());
            data.setMonthLabel(exp.getMonthsField());
            data.setWeekLabel(exp.getDaysOfWeekField());
            data.setYearLabel(exp.getYearField());
            
            if (null == exp.getTimeAfter(dd)) {
                log.warn("Cron Expression [{}] are set the trigger before current date, it will never be trigger!", 
                    request.getCronExpression());
                
                TreeSet<Integer> yearSet = exp.getYearSet();
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.YEAR, yearSet.first());
                dd = calendar.getTime();
            }
            
            data.setStartDate(DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd));
            
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
            data.setSchedulerNextResults(schedulerNextResults);
            
            return ResponseEntity.ok(CronResponse.success(data));
            
        } catch (ParseException e) {
            log.error("解析Cron表达式失败", e);
            return ResponseEntity.ok(CronResponse.<CronData>error(500, "解析Cron表达式失败: " + e.getMessage()));
        }
    }

    @PostMapping("/generateCronExp")
    public ResponseEntity<CronResponse<CronData>> generateCronExp(@Valid @RequestBody CronGenerateRequest request) {
        try {
            String secLabel = "0";
            String minLabel, hhLabel, dayLabel, weekLabel, monthLabel, yearLabel;
            
            // Minute expression
            if (PER_TYPE.equals(request.getMinuteexptype())) {
                minLabel = request.getStartMinute() + "/" + request.getEveryMinute();
            } else {
                minLabel = String.join(",", request.getAssignMins().stream()
                    .map(String::valueOf)
                    .toArray(String[]::new));
            }
            
            // Hours
            if (PER_TYPE.equals(request.getHourexptype())) {
                hhLabel = "*";
            } else {
                hhLabel = String.join(",", request.getAssignHours().stream()
                    .map(String::valueOf)
                    .toArray(String[]::new));
            }
            
            // Days/Weeks
            if ("1".equals(request.getUseweekck())) {
                dayLabel = "?";
                if (PER_TYPE.equals(request.getWeekexptype())) {
                    weekLabel = "*";
                } else {
                    weekLabel = String.join(",", request.getAssignWeeks().stream()
                        .map(String::valueOf)
                        .toArray(String[]::new));
                }
            } else {
                weekLabel = "?";
                if (PER_TYPE.equals(request.getDayexptype())) {
                    dayLabel = "*";
                } else {
                    dayLabel = String.join(",", request.getAssignDays().stream()
                        .map(String::valueOf)
                        .toArray(String[]::new));
                }
            }
            
            // Months
            if (PER_TYPE.equals(request.getMonthexptype())) {
                monthLabel = "*";
            } else {
                monthLabel = String.join(",", request.getAssignMonths().stream()
                    .map(String::valueOf)
                    .toArray(String[]::new));
            }
            
            // Year
            boolean useYearCorn = false;
            if ("1".equals(request.getUseyearck()) && 
                ASSIGN_TYPE.equals(request.getYearexptype()) && 
                request.getAssignYearCron() != null && !request.getAssignYearCron().isEmpty()) {
                useYearCorn = true;
                yearLabel = request.getAssignYearCron();
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
            CronData data = new CronData();
            data.setCronExpression(cronExpression);
            data.setStartDate(DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd));
            
            List<String> schedulerNextResults = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                dd = exp.getNextValidTimeAfter(dd);
                schedulerNextResults.add(DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd));
                dd = new Date(dd.getTime() + 1000);
            }
            data.setSchedulerNextResults(schedulerNextResults);
            
            return ResponseEntity.ok(CronResponse.success(data));
            
        } catch (Exception e) {
            log.error("生成Cron表达式失败", e);
            return ResponseEntity.ok(CronResponse.<CronData>error(500, "生成Cron表达式失败: " + e.getMessage()));
        }
    }
}
