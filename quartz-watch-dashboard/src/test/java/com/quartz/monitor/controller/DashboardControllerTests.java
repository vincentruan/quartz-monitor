package com.quartz.monitor.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import com.quartz.monitor.dto.response.DashboardStatsResponse;
import com.quartz.monitor.exception.SchedulerNotFoundException;
import com.quartz.monitor.service.SchedulerService;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTests {

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private DashboardController dashboardController;

    private static final String PRIMARY_SCHEDULER_NAME = "QuartzMonitorScheduler";

    @BeforeEach
    void setUp() throws SchedulerException {
        // Common mocking for schedulerService returning our mocked scheduler
        when(schedulerService.getSchedulerByName(PRIMARY_SCHEDULER_NAME)).thenReturn(scheduler);
    }

    @Test
    void testGetStats_Successful() throws SchedulerException {
        // Arrange
        when(schedulerService.getActiveInstanceCount(PRIMARY_SCHEDULER_NAME)).thenReturn(2);

        when(scheduler.getSchedulerName()).thenReturn(PRIMARY_SCHEDULER_NAME);
        when(scheduler.isStarted()).thenReturn(true);

        List<String> jobGroupNames = Arrays.asList("group1", "group2");
        when(scheduler.getJobGroupNames()).thenReturn(jobGroupNames);

        Set<JobKey> group1JobKeys = new HashSet<>(Arrays.asList(new JobKey("job1", "group1"), new JobKey("job2", "group1")));
        Set<JobKey> group2JobKeys = new HashSet<>(Collections.singletonList(new JobKey("job3", "group2")));
        when(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("group1"))).thenReturn(group1JobKeys);
        when(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("group2"))).thenReturn(group2JobKeys);

        List<String> triggerGroupNames = Arrays.asList("triggerGroup1");
        when(scheduler.getTriggerGroupNames()).thenReturn(triggerGroupNames);
        Set<TriggerKey> triggerKeys = new HashSet<>(Arrays.asList(new TriggerKey("trigger1", "triggerGroup1")));
        when(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("triggerGroup1"))).thenReturn(triggerKeys);

        @SuppressWarnings("unchecked") // Suppress warning for mock type
        List<JobExecutionContext> mockExecutingJobs = mock(List.class);
        when(mockExecutingJobs.size()).thenReturn(1);
        when(scheduler.getCurrentlyExecutingJobs()).thenReturn(mockExecutingJobs);


        // Act
        DashboardStatsResponse response = dashboardController.getStats();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getInstanceCount());
        assertEquals(1, response.getSchedulerCount());
        assertEquals(3, response.getJobCount()); // job1, job2, job3
        assertEquals(1, response.getTriggerCount()); // trigger1
        assertEquals(1, response.getRunningJobCount());

        verify(schedulerService).getSchedulerByName(PRIMARY_SCHEDULER_NAME);
        verify(schedulerService).getActiveInstanceCount(PRIMARY_SCHEDULER_NAME);
        verify(scheduler).getJobGroupNames();
        verify(scheduler).getTriggerGroupNames();
        verify(scheduler).getCurrentlyExecutingJobs();
    }

    @Test
    void testGetStats_SchedulerServiceThrowsSchedulerNotFoundException() throws SchedulerException {
        // Arrange
        when(schedulerService.getSchedulerByName(PRIMARY_SCHEDULER_NAME))
            .thenThrow(new SchedulerNotFoundException("Scheduler not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dashboardController.getStats();
        });
        assertTrue(exception.getMessage().contains("Failed to fetch dashboard statistics"));
        assertTrue(exception.getCause() instanceof SchedulerNotFoundException);
    }

    @Test
    void testGetStats_SchedulerServiceGetActiveInstanceCountThrowsException() throws SchedulerException {
        // Arrange
        // schedulerService.getSchedulerByName is already stubbed in setUp to return scheduler mock
        when(schedulerService.getActiveInstanceCount(PRIMARY_SCHEDULER_NAME))
            .thenThrow(new SchedulerNotFoundException("Cannot get instance count")); // Or any other runtime ex

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dashboardController.getStats();
        });
        assertTrue(exception.getMessage().contains("Failed to fetch dashboard statistics"));
        // The cause could be the direct exception from getActiveInstanceCount
        assertTrue(exception.getCause() instanceof SchedulerNotFoundException);
    }


    @Test
    void testGetStats_SchedulerGetJobGroupNamesThrowsSchedulerException() throws SchedulerException {
        // Arrange
        // schedulerService.getSchedulerByName is stubbed in setUp
        when(schedulerService.getActiveInstanceCount(PRIMARY_SCHEDULER_NAME)).thenReturn(1); // Needs to pass this
        when(scheduler.isStarted()).thenReturn(true); // Needs to pass this

        when(scheduler.getJobGroupNames()).thenThrow(new SchedulerException("Failed to get job groups"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dashboardController.getStats();
        });
        assertTrue(exception.getMessage().contains("Failed to fetch dashboard statistics"));
        assertTrue(exception.getCause() instanceof SchedulerException);
    }

    @Test
    void testGetStats_SchedulerGetTriggerGroupNamesThrowsSchedulerException() throws SchedulerException {
        // Arrange
        when(schedulerService.getActiveInstanceCount(PRIMARY_SCHEDULER_NAME)).thenReturn(1);
        when(scheduler.isStarted()).thenReturn(true);
        when(scheduler.getJobGroupNames()).thenReturn(Collections.singletonList("group1"));
        when(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("group1"))).thenReturn(Collections.singleton(new JobKey("job1", "group1")));

        when(scheduler.getTriggerGroupNames()).thenThrow(new SchedulerException("Failed to get trigger groups"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dashboardController.getStats();
        });
        assertTrue(exception.getMessage().contains("Failed to fetch dashboard statistics"));
        assertTrue(exception.getCause() instanceof SchedulerException);
    }

    @Test
    void testGetStats_SchedulerGetCurrentlyExecutingJobsThrowsSchedulerException() throws SchedulerException {
        // Arrange
        when(schedulerService.getActiveInstanceCount(PRIMARY_SCHEDULER_NAME)).thenReturn(1);
        when(scheduler.isStarted()).thenReturn(true);
        when(scheduler.getJobGroupNames()).thenReturn(Collections.singletonList("group1"));
        when(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("group1"))).thenReturn(Collections.singleton(new JobKey("job1", "group1")));
        when(scheduler.getTriggerGroupNames()).thenReturn(Collections.singletonList("triggerGroup1"));
        when(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("triggerGroup1"))).thenReturn(Collections.singleton(new TriggerKey("trigger1","triggerGroup1")));

        when(scheduler.getCurrentlyExecutingJobs()).thenThrow(new SchedulerException("Failed to get executing jobs"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dashboardController.getStats();
        });
        assertTrue(exception.getMessage().contains("Failed to fetch dashboard statistics"));
        assertTrue(exception.getCause() instanceof SchedulerException);
    }
}
