package com.quartz.monitor.dto;

import java.util.List;
import javax.validation.constraints.NotNull;

public class CronGenerateRequest {
    private String minuteexptype = "0";
    private Integer startMinute;
    private Integer everyMinute;
    private List<@NotNull Integer> assignMins;
    
    private String hourexptype;
    private List<@NotNull Integer> assignHours;
    
    private String dayexptype;
    private List<@NotNull Integer> assignDays;
    
    private String monthexptype; 
    private List<@NotNull Integer> assignMonths;
    
    private String useweekck;
    private String weekexptype;
    private List<@NotNull Integer> assignWeeks;
    
    private String useyearck;
    private String yearexptype;
    private String assignYearCron;

    // Getters and setters
    public String getMinuteexptype() { return minuteexptype; }
    public void setMinuteexptype(String minuteexptype) { this.minuteexptype = minuteexptype; }
    public Integer getStartMinute() { return startMinute; }
    public void setStartMinute(Integer startMinute) { this.startMinute = startMinute; }
    public Integer getEveryMinute() { return everyMinute; }
    public void setEveryMinute(Integer everyMinute) { this.everyMinute = everyMinute; }
    public List<Integer> getAssignMins() { return assignMins; }
    public void setAssignMins(List<Integer> assignMins) { this.assignMins = assignMins; }
    public String getHourexptype() { return hourexptype; }
    public void setHourexptype(String hourexptype) { this.hourexptype = hourexptype; }
    public List<Integer> getAssignHours() { return assignHours; }
    public void setAssignHours(List<Integer> assignHours) { this.assignHours = assignHours; }
    public String getDayexptype() { return dayexptype; }
    public void setDayexptype(String dayexptype) { this.dayexptype = dayexptype; }
    public List<Integer> getAssignDays() { return assignDays; }
    public void setAssignDays(List<Integer> assignDays) { this.assignDays = assignDays; }
    public String getMonthexptype() { return monthexptype; }
    public void setMonthexptype(String monthexptype) { this.monthexptype = monthexptype; }
    public List<Integer> getAssignMonths() { return assignMonths; }
    public void setAssignMonths(List<Integer> assignMonths) { this.assignMonths = assignMonths; }
    public String getUseweekck() { return useweekck; }
    public void setUseweekck(String useweekck) { this.useweekck = useweekck; }
    public String getWeekexptype() { return weekexptype; }
    public void setWeekexptype(String weekexptype) { this.weekexptype = weekexptype; }
    public List<Integer> getAssignWeeks() { return assignWeeks; }
    public void setAssignWeeks(List<Integer> assignWeeks) { this.assignWeeks = assignWeeks; }
    public String getUseyearck() { return useyearck; }
    public void setUseyearck(String useyearck) { this.useyearck = useyearck; }
    public String getYearexptype() { return yearexptype; }
    public void setYearexptype(String yearexptype) { this.yearexptype = yearexptype; }
    public String getAssignYearCron() { return assignYearCron; }
    public void setAssignYearCron(String assignYearCron) { this.assignYearCron = assignYearCron; }
}
