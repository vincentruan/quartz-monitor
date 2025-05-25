package com.quartz.monitor.dto.request;

import javax.validation.constraints.NotBlank;

public class CronParseRequest {
    @NotBlank(message = "Cron expression cannot be blank")
    private String cronExpression;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
