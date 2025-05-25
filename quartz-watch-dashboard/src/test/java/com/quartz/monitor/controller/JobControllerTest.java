package com.quartz.monitor.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.matchers.GroupMatcher;

import com.quartz.monitor.dto.JobInfo;
import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.request.CreateJobRequest;
import com.quartz.monitor.dto.request.UpdateJobRequest;
import com.quartz.monitor.dto.response.JobDetailResponse;
import com.quartz.monitor.exception.JobNotFoundException;
import com.quartz.monitor.exception.SchedulerNotFoundException;
import com.quartz.monitor.service.JobService;

/**
 * Unit tests for JobController
 */
@ExtendWith(MockitoExtension.class)
class JobControllerTest {
    
    @Mock
    private JobService jobService;
    
    @InjectMocks
    private JobController jobController;
    
    private JobInfo testJobInfo;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        testJobInfo = new JobInfo();
        testJobInfo.setSchedulerName("defaultScheduler");
        testJobInfo.setSchedulerInstanceId("instance1");
        testJobInfo.setJobName("testJob");
        testJobInfo.setJobGroup("testGroup");
        testJobInfo.setJobClassName("com.example.TestJob");
        testJobInfo.setDescription("Test job description");
    }
    
    @Test
    void testGetJobs_Success() {
        // Arrange
        PageResult<JobInfo> expectedResult = new PageResult<>(
            Arrays.asList(testJobInfo), 0, 20, 1
        );
        
        when(jobService.getJobs(0, 20, null, null)).thenReturn(expectedResult);
        
        // Act
        PageResult<JobInfo> result = jobController.getJobs(0, 20, null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("testJob", result.getContent().get(0).getJobName());
        
        verify(jobService).getJobs(0, 20, null, null);
    }
    
    @Test
    void testGetJobs_WithFilters() {
        // Arrange
        PageResult<JobInfo> expectedResult = new PageResult<>(
            Arrays.asList(testJobInfo), 0, 20, 1
        );
        
        when(jobService.getJobs(0, 20, "defaultScheduler", "testGroup"))
            .thenReturn(expectedResult);
        
        // Act
        PageResult<JobInfo> result = jobController.getJobs(0, 20, "defaultScheduler", "testGroup");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        
        verify(jobService).getJobs(0, 20, "defaultScheduler", "testGroup");
    }
    
    @Test
    void testGetJobDetail_Success() {
        // Arrange
        JobDetailResponse expectedResponse = new JobDetailResponse(testJobInfo, Arrays.asList());
        
        when(jobService.getJobDetail("defaultScheduler", "testGroup", "testJob"))
            .thenReturn(expectedResponse);
        
        // Act
        JobDetailResponse result = jobController.getJobDetail("defaultScheduler", "testGroup", "testJob");
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getJobInfo());
        assertEquals("testJob", result.getJobInfo().getJobName());
        
        verify(jobService).getJobDetail("defaultScheduler", "testGroup", "testJob");
    }
    
    @Test
    void testCreateJob_Success() {
        // Arrange
        CreateJobRequest request = new CreateJobRequest();
        request.setSchedulerName("defaultScheduler");
        request.setJobName("newJob");
        request.setJobGroup("newGroup");
        request.setJobClassName("com.example.NewJob");
        request.setDescription("New job");
        
        when(jobService.createJob(request)).thenReturn(testJobInfo);
        
        // Act
        JobInfo result = jobController.createJob(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("testJob", result.getJobName());
        
        verify(jobService).createJob(request);
    }
    
    @Test
    void testUpdateJob_Success() {
        // Arrange
        UpdateJobRequest request = new UpdateJobRequest();
        request.setDescription("Updated description");
        
        when(jobService.updateJob("defaultScheduler", "testGroup", "testJob", request))
            .thenReturn(testJobInfo);
        
        // Act
        JobInfo result = jobController.updateJob("defaultScheduler", "testGroup", "testJob", request);
        
        // Assert
        assertNotNull(result);
        assertEquals("testJob", result.getJobName());
        
        verify(jobService).updateJob("defaultScheduler", "testGroup", "testJob", request);
    }
    
    @Test
    void testDeleteJob_Success() {
        // Arrange
        doNothing().when(jobService).deleteJob("defaultScheduler", "testGroup", "testJob");
        
        // Act
        jobController.deleteJob("defaultScheduler", "testGroup", "testJob");
        
        // Assert
        verify(jobService).deleteJob("defaultScheduler", "testGroup", "testJob");
    }
    
    @Test
    void testPauseJob_Success() {
        // Arrange
        doNothing().when(jobService).pauseJob("defaultScheduler", "testGroup", "testJob");
        
        // Act
        jobController.pauseJob("defaultScheduler", "testGroup", "testJob");
        
        // Assert
        verify(jobService).pauseJob("defaultScheduler", "testGroup", "testJob");
    }
    
    @Test
    void testResumeJob_Success() {
        // Arrange
        doNothing().when(jobService).resumeJob("defaultScheduler", "testGroup", "testJob");
        
        // Act
        jobController.resumeJob("defaultScheduler", "testGroup", "testJob");
        
        // Assert
        verify(jobService).resumeJob("defaultScheduler", "testGroup", "testJob");
    }
    
    @Test
    void testTriggerJob_Success() {
        // Arrange
        doNothing().when(jobService).triggerJob("defaultScheduler", "testGroup", "testJob");
        
        // Act
        jobController.triggerJob("defaultScheduler", "testGroup", "testJob");
        
        // Assert
        verify(jobService).triggerJob("defaultScheduler", "testGroup", "testJob");
    }
} 