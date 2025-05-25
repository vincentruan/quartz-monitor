package com.quartz.monitor.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.quartz.monitor.dto.request.AddTriggerRequest;

/**
 * Custom validator for trigger requests
 */
@Component
public class TriggerValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AddTriggerRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AddTriggerRequest request = (AddTriggerRequest) target;
        
        if (request.getDateFlag() != null) {
            if (request.getDateFlag() == 1) {
                // Simple trigger - requires date
                if (request.getDate() == null) {
                    errors.rejectValue("date", "required", "Date is required for simple trigger");
                }
            } else {
                // Cron trigger - requires cron expression
                if (request.getCron() == null || request.getCron().trim().isEmpty()) {
                    errors.rejectValue("cron", "required", "Cron expression is required for cron trigger");
                }
            }
        }
    }
} 