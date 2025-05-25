package com.quartz.monitor.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.TriggerInfo;
import com.quartz.monitor.dto.request.CreateTriggerRequest;
import com.quartz.monitor.dto.request.UpdateTriggerRequest;
import com.quartz.monitor.dto.response.TriggerDetailResponse;
import com.quartz.monitor.exception.JobNotFoundException;
import com.quartz.monitor.exception.SchedulerNotFoundException;
import com.quartz.monitor.exception.TriggerNotFoundException;
import com.quartz.monitor.service.TriggerService;

/**
 * Unit tests for TriggerController
 */
@ExtendWith(MockitoExtension.class)
class TriggerControllerTest {
    
    @Mock
    private TriggerService triggerService;
    
    @InjectMocks
    private TriggerController triggerController;
    
    private JobKey testJobKey;
    private TriggerKey testTriggerKey;
    private CronTriggerImpl cronTrigger;
    private SimpleTriggerImpl simpleTrigger;
    
    @BeforeEach
    void setUp() throws SchedulerException, ParseException {
        // Setup test data
        testJobKey = new JobKey("testJob", "testGroup");
        testTriggerKey = new TriggerKey("testTrigger", "testTriggerGroup");
        
        // Setup cron trigger
        cronTrigger = new CronTriggerImpl();
        cronTrigger.setKey(testTriggerKey);
        cronTrigger.setJobKey(testJobKey);
        cronTrigger.setDescription("Test cron trigger");
        cronTrigger.setCronExpression("0 0 12 * * ?");
        cronTrigger.setStartTime(new Date());
        
        // Setup simple trigger
        simpleTrigger = new SimpleTriggerImpl();
        simpleTrigger.setKey(new TriggerKey("simpleTrigger", "testTriggerGroup"));
        simpleTrigger.setJobKey(testJobKey);
        simpleTrigger.setDescription("Test simple trigger");
        simpleTrigger.setStartTime(new Date());
        simpleTrigger.setRepeatCount(5);
        simpleTrigger.setRepeatInterval(60000);
        
        // Setup scheduler mocks
        when(triggerService.getSchedulerByName("defaultScheduler")).thenReturn(defaultScheduler);
        when(defaultScheduler.checkExists(testJobKey)).thenReturn(true);
        List<? extends Trigger> triggers = Arrays.asList(cronTrigger, simpleTrigger);
        doReturn(triggers).when(defaultScheduler).getTriggersOfJob(testJobKey);
        when(defaultScheduler.getTriggerState(any(TriggerKey.class))).thenReturn(Trigger.TriggerState.NORMAL);
    }
    
    @Test
    void testGetTriggersForJob_Success() throws SchedulerException {
        // Arrange
        PageResult<TriggerInfo> expectedResult = new PageResult<>(
            Arrays.asList(testTriggerInfo), 0, 20, 1
        );
        
        when(triggerService.getTriggersForJob("defaultScheduler", "testGroup", "testJob", 0, 20))
            .thenReturn(expectedResult);
        
        // Act
        PageResult<TriggerInfo> result = triggerController.getTriggersForJob(
            "defaultScheduler", "testGroup", "testJob", 0, 20);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("testTrigger", result.getContent().get(0).getTriggerName());
        
        verify(triggerService).getTriggersForJob("defaultScheduler", "testGroup", "testJob", 0, 20);
    }
    
    @Test
    void testGetTriggersForJob_JobNotFound() throws SchedulerException {
        // Arrange
        when(triggerService.getSchedulerByName("defaultScheduler")).thenReturn(defaultScheduler);
        when(defaultScheduler.checkExists(testJobKey)).thenReturn(false);
        
        // Act & Assert
        assertThrows(JobNotFoundException.class, () -> 
            triggerController.getTriggersForJob("defaultScheduler", "testGroup", "testJob", 0, 20)
        );
    }
    
    @Test
    void testGetTriggerDetail_Success() throws SchedulerException {
        // Arrange
        TriggerDetailResponse expectedResponse = new TriggerDetailResponse(testTriggerInfo);
        
        when(triggerService.getTriggerDetail("defaultScheduler", "testTriggerGroup", "testTrigger"))
            .thenReturn(expectedResponse);
        
        // Act
        TriggerDetailResponse result = triggerController.getTriggerDetail(
            "defaultScheduler", "testTriggerGroup", "testTrigger");
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getTriggerInfo());
        assertEquals("testTrigger", result.getTriggerInfo().getTriggerName());
        
        verify(triggerService).getTriggerDetail("defaultScheduler", "testTriggerGroup", "testTrigger");
    }
    
    @Test
    void testGetTriggerDetail_NotFound() throws SchedulerException {
        // Arrange
        when(triggerService.getSchedulerByName("defaultScheduler")).thenReturn(defaultScheduler);
        when(defaultScheduler.getTrigger(testTriggerKey)).thenReturn(null);
        
        // Act & Assert
        assertThrows(TriggerNotFoundException.class, () -> 
            triggerController.getTriggerDetail("defaultScheduler", "testTriggerGroup", "testTrigger")
        );
    }
    
    @Test
    void testCreateCronTrigger_Success() throws Exception {
        // Arrange
        CreateTriggerRequest request = new CreateTriggerRequest();
        request.setSchedulerName("defaultScheduler");
        request.setTriggerName("newCronTrigger");
        request.setTriggerGroup("newGroup");
        request.setJobName("testJob");
        request.setJobGroup("testGroup");
        request.setDescription("New cron trigger");
        request.setTriggerType("CRON");
        request.setCronExpression("0 0 12 * * ?");
        request.setStartTime(new Date());
        
        when(triggerService.createTrigger(request)).thenReturn(testTriggerInfo);
        
        // Act
        TriggerInfo result = triggerController.createTrigger(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("testTrigger", result.getTriggerName());
        
        verify(triggerService).createTrigger(request);
    }
    
    @Test
    void testCreateSimpleTrigger_Success() throws Exception {
        // Arrange
        CreateTriggerRequest request = new CreateTriggerRequest();
        request.setSchedulerName("defaultScheduler");
        request.setTriggerName("newSimpleTrigger");
        request.setTriggerGroup("newGroup");
        request.setJobName("testJob");
        request.setJobGroup("testGroup");
        request.setDescription("New simple trigger");
        request.setTriggerType("SIMPLE");
        request.setRepeatCount(5);
        request.setRepeatInterval(60000L);
        request.setStartTime(new Date());
        
        when(triggerService.createTrigger(request)).thenReturn(testTriggerInfo);
        
        // Act
        TriggerInfo result = triggerController.createTrigger(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("SIMPLE", result.getTriggerType());
        
        verify(triggerService).createTrigger(request);
    }
    
    @Test
    void testCreateTrigger_InvalidCronExpression() throws SchedulerException {
        // Arrange
        CreateTriggerRequest request = new CreateTriggerRequest();
        request.setSchedulerName("defaultScheduler");
        request.setTriggerName("newCronTrigger");
        request.setTriggerGroup("newGroup");
        request.setJobName("testJob");
        request.setJobGroup("testGroup");
        request.setTriggerType("CRON");
        request.setCronExpression("invalid cron");
        
        when(triggerService.getSchedulerByName("defaultScheduler")).thenReturn(defaultScheduler);
        when(defaultScheduler.checkExists(testJobKey)).thenReturn(true);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            triggerController.createTrigger(request)
        );
    }
    
    @Test
    void testUpdateTrigger_Success() throws Exception {
        // Arrange
        UpdateTriggerRequest request = new UpdateTriggerRequest();
        request.setDescription("Updated description");
        request.setPriority(10);
        
        when(triggerService.updateTrigger("defaultScheduler", "testTriggerGroup", "testTrigger", request))
            .thenReturn(testTriggerInfo);
        
        // Act
        TriggerInfo result = triggerController.updateTrigger(
            "defaultScheduler", "testTriggerGroup", "testTrigger", request);
        
        // Assert
        assertNotNull(result);
        assertEquals("testTrigger", result.getTriggerName());
        
        verify(triggerService).updateTrigger("defaultScheduler", "testTriggerGroup", "testTrigger", request);
    }
    
    @Test
    void testDeleteTrigger_Success() throws SchedulerException {
        // Arrange
        doNothing().when(triggerService).deleteTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
        
        // Act
        triggerController.deleteTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
        
        // Assert
        verify(triggerService).deleteTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
    }
    
    @Test
    void testDeleteTrigger_NotFound() throws SchedulerException {
        // Arrange
        when(triggerService.getSchedulerByName("defaultScheduler")).thenReturn(defaultScheduler);
        when(defaultScheduler.checkExists(testTriggerKey)).thenReturn(false);
        
        // Act & Assert
        assertThrows(TriggerNotFoundException.class, () -> 
            triggerController.deleteTrigger("defaultScheduler", "testTriggerGroup", "testTrigger")
        );
    }
    
    @Test
    void testPauseTrigger_Success() throws SchedulerException {
        // Arrange
        doNothing().when(triggerService).pauseTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
        
        // Act
        triggerController.pauseTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
        
        // Assert
        verify(triggerService).pauseTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
    }
    
    @Test
    void testResumeTrigger_Success() throws SchedulerException {
        // Arrange
        doNothing().when(triggerService).resumeTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
        
        // Act
        triggerController.resumeTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
        
        // Assert
        verify(triggerService).resumeTrigger("defaultScheduler", "testTriggerGroup", "testTrigger");
    }
    
    @Test
    void testSchedulerNotFound() throws SchedulerException {
        // Arrange
        when(triggerService.getSchedulerByName("nonExistentScheduler"))
            .thenThrow(new SchedulerNotFoundException("Scheduler not found"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            triggerController.getTriggerDetail("nonExistentScheduler", "testTriggerGroup", "testTrigger")
        );
    }
} 