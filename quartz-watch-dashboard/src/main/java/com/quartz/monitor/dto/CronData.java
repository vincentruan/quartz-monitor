package com.quartz.monitor.dto;

import java.util.List;

public class CronData {
    private String cronExpression;
    private String startDate;
    private List<String> schedulerNextResults;
    private String secLabel;
    private String minLabel;
    private String hhLabel;
    private String dayLabel;
    private String monthLabel;
    private String weekLabel;
    private String yearLabel;

    // Getters and Setters
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public List<String> getSchedulerNextResults() { return schedulerNextResults; }
    public void setSchedulerNextResults(List<String> schedulerNextResults) { this.schedulerNextResults = schedulerNextResults; }
    public String getSecLabel() { return secLabel; }
    public void setSecLabel(String secLabel) { this.secLabel = secLabel; }
    public String getMinLabel() { return minLabel; }
    public void setMinLabel(String minLabel) { this.minLabel = minLabel; }
    public String getHhLabel() { return hhLabel; }
    public void setHhLabel(String hhLabel) { this.hhLabel = hhLabel; }
    public String getDayLabel() { return dayLabel; }
    public void setDayLabel(String dayLabel) { this.dayLabel = dayLabel; }
    public String getMonthLabel() { return monthLabel; }
    public void setMonthLabel(String monthLabel) { this.monthLabel = monthLabel; }
    public String getWeekLabel() { return weekLabel; }
    public void setWeekLabel(String weekLabel) { this.weekLabel = weekLabel; }
    public String getYearLabel() { return yearLabel; }
    public void setYearLabel(String yearLabel) { this.yearLabel = yearLabel; }
}
