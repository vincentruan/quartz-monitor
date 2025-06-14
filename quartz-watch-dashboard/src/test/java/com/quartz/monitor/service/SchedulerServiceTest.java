package com.quartz.monitor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.SchedulerRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import com.quartz.monitor.exception.SchedulerNotFoundException;

/**
 * Unit tests for SchedulerService
 */
@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {
    
    @Mock
    private Scheduler defaultScheduler;
    
    @Mock
    private Scheduler scheduler1;
    
    @Mock
    private Scheduler scheduler2;
    
    @Mock
    private SchedulerRepository schedulerRepository;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private SchedulerMetaData schedulerMetaData;
    
    @InjectMocks
    private SchedulerService schedulerService;

    private static final String TEST_SCHEDULER_NAME = "testScheduler";
    
    @BeforeEach
    void setUp() throws SchedulerException {
        // Common setup for schedulers
        when(defaultScheduler.getSchedulerName()).thenReturn("defaultScheduler");
        lenient().when(defaultScheduler.isStarted()).thenReturn(true); // Use lenient for flexibility
        lenient().when(defaultScheduler.getMetaData()).thenReturn(schedulerMetaData);


        when(scheduler1.getSchedulerName()).thenReturn(TEST_SCHEDULER_NAME);
        lenient().when(scheduler1.isStarted()).thenReturn(true);
        lenient().when(scheduler1.getMetaData()).thenReturn(schedulerMetaData);
        
        when(scheduler2.getSchedulerName()).thenReturn("scheduler2");
        lenient().when(scheduler2.isStarted()).thenReturn(false);
        lenient().when(scheduler2.getMetaData()).thenReturn(schedulerMetaData);

        // Ensure SchedulerService is created with the mock jdbcTemplate if @InjectMocks doesn't cover it due to constructor changes
        // This might be needed if the test runner doesn't re-initialize schedulerService with all mocks post-constructor update
        schedulerService = new SchedulerService(Arrays.asList(scheduler1, scheduler2, defaultScheduler), defaultScheduler, namedParameterJdbcTemplate);

    }
    
    @Test
    void testGetAllSchedulers_FromRepository() {
        // Arrange
        List<Scheduler> repositorySchedulers = Arrays.asList(scheduler1, scheduler2);
        
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookupAll()).thenReturn(repositorySchedulers);
            
            // Act
            Collection<Scheduler> result = schedulerService.getAllSchedulers();
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(scheduler1));
            assertTrue(result.contains(scheduler2));
        }
    }
    
    @Test
    void testGetAllSchedulers_FromInjectedSchedulers() {
        // Arrange
        ReflectionTestUtils.setField(schedulerService, "schedulers", Arrays.asList(scheduler1, scheduler2));
        
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookupAll()).thenReturn(null);
            
            // Act
            Collection<Scheduler> result = schedulerService.getAllSchedulers();
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(scheduler1));
            assertTrue(result.contains(scheduler2));
        }
    }
    
    @Test
    void testGetAllSchedulers_DefaultSchedulerFallback() {
        // Arrange
        ReflectionTestUtils.setField(schedulerService, "schedulers", null);
        
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookupAll()).thenReturn(Collections.emptyList());
            
            // Act
            Collection<Scheduler> result = schedulerService.getAllSchedulers();
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.contains(defaultScheduler));
        }
    }
    
    @Test
    void testGetSchedulerByName_FromRepository() {
        // Arrange
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup("scheduler1")).thenReturn(scheduler1);
            
            // Act
            Scheduler result = schedulerService.getSchedulerByName("scheduler1");
            
            // Assert
            assertNotNull(result);
            assertEquals(scheduler1, result);
        }
    }
    
    @Test
    void testGetSchedulerByName_FromInjectedSchedulers() throws SchedulerException {
        // Arrange
        ReflectionTestUtils.setField(schedulerService, "schedulers", Arrays.asList(scheduler1, scheduler2));
        
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup("scheduler1")).thenReturn(null);
            
            // Act
            Scheduler result = schedulerService.getSchedulerByName("scheduler1");
            
            // Assert
            assertNotNull(result);
            assertEquals(scheduler1, result);
        }
    }
    
    @Test
    void testGetSchedulerByName_DefaultScheduler() throws SchedulerException {
        // Arrange
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup("defaultScheduler")).thenReturn(null);
            
            // Act
            Scheduler result = schedulerService.getSchedulerByName("defaultScheduler");
            
            // Assert
            assertNotNull(result);
            assertEquals(defaultScheduler, result);
        }
    }
    
    @Test
    void testGetSchedulerByName_NotFound() {
        // Arrange
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup("nonExistent")).thenReturn(null);
            
            // Act & Assert
            assertThrows(SchedulerNotFoundException.class, () -> 
                schedulerService.getSchedulerByName("nonExistent")
            );
        }
    }
    
    @Test
    void testGetStartedSchedulers() throws SchedulerException {
        // Arrange
        List<Scheduler> allSchedulers = Arrays.asList(scheduler1, scheduler2, defaultScheduler);
        
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookupAll()).thenReturn(allSchedulers);
            
            // Act
            Collection<Scheduler> result = schedulerService.getStartedSchedulers();
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size()); // scheduler1 and defaultScheduler are started
            assertTrue(result.contains(scheduler1));
            assertTrue(result.contains(defaultScheduler));
            assertFalse(result.contains(scheduler2)); // scheduler2 is not started
        }
    }
    
    @Test
    void testGetStartedSchedulers_WithException() throws SchedulerException {
        // Arrange
        when(scheduler1.isStarted()).thenThrow(new SchedulerException("Error"));
        ReflectionTestUtils.setField(schedulerService, "schedulers", Arrays.asList(scheduler1, defaultScheduler));
        
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookupAll()).thenReturn(null);
            
            // Act
            Collection<Scheduler> result = schedulerService.getStartedSchedulers();
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size()); // Only defaultScheduler should be returned
            assertTrue(result.contains(defaultScheduler));
        }
    }

    // --- Tests for getActiveInstanceCount ---

    @Test
    void testGetActiveInstanceCount_Clustered_MultipleInstances() throws SchedulerException {
        // Arrange
        when(schedulerMetaData.isJobStoreClustered()).thenReturn(true);
        when(namedParameterJdbcTemplate.queryForObject(any(String.class), any(Map.class), eq(Integer.class)))
            .thenReturn(3);

        // Mock SchedulerRepository to return the scheduler1 which has TEST_SCHEDULER_NAME
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(TEST_SCHEDULER_NAME)).thenReturn(scheduler1);

            // Act
            int count = schedulerService.getActiveInstanceCount(TEST_SCHEDULER_NAME);

            // Assert
            assertEquals(3, count);
        }
    }

    @Test
    void testGetActiveInstanceCount_Clustered_OneInstance() throws SchedulerException {
        // Arrange
        when(schedulerMetaData.isJobStoreClustered()).thenReturn(true);
        when(namedParameterJdbcTemplate.queryForObject(any(String.class), any(Map.class), eq(Integer.class)))
            .thenReturn(1);

        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(TEST_SCHEDULER_NAME)).thenReturn(scheduler1);

            // Act
            int count = schedulerService.getActiveInstanceCount(TEST_SCHEDULER_NAME);

            // Assert
            assertEquals(1, count);
        }
    }

    @Test
    void testGetActiveInstanceCount_Clustered_NoInstances() throws SchedulerException {
        // Arrange
        when(schedulerMetaData.isJobStoreClustered()).thenReturn(true);
        when(namedParameterJdbcTemplate.queryForObject(any(String.class), any(Map.class), eq(Integer.class)))
            .thenReturn(0);

        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(TEST_SCHEDULER_NAME)).thenReturn(scheduler1);

            // Act
            int count = schedulerService.getActiveInstanceCount(TEST_SCHEDULER_NAME);

            // Assert
            assertEquals(0, count);
        }
    }

    @Test
    void testGetActiveInstanceCount_NonClustered_Started() throws SchedulerException {
        // Arrange
        when(schedulerMetaData.isJobStoreClustered()).thenReturn(false);
        when(scheduler1.isStarted()).thenReturn(true); // scheduler1 is TEST_SCHEDULER_NAME

        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(TEST_SCHEDULER_NAME)).thenReturn(scheduler1);

            // Act
            int count = schedulerService.getActiveInstanceCount(TEST_SCHEDULER_NAME);

            // Assert
            assertEquals(1, count);
            verify(namedParameterJdbcTemplate, never()).queryForObject(any(String.class), any(Map.class), eq(Integer.class));
        }
    }

    @Test
    void testGetActiveInstanceCount_NonClustered_NotStarted() throws SchedulerException {
        // Arrange
        when(schedulerMetaData.isJobStoreClustered()).thenReturn(false);
        when(scheduler1.isStarted()).thenReturn(false); // scheduler1 is TEST_SCHEDULER_NAME

        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(TEST_SCHEDULER_NAME)).thenReturn(scheduler1);

            // Act
            int count = schedulerService.getActiveInstanceCount(TEST_SCHEDULER_NAME);

            // Assert
            assertEquals(0, count);
            verify(namedParameterJdbcTemplate, never()).queryForObject(any(String.class), any(Map.class), eq(Integer.class));
        }
    }

    @Test
    void testGetActiveInstanceCount_JdbcTemplateThrowsException() throws SchedulerException {
        // Arrange
        when(schedulerMetaData.isJobStoreClustered()).thenReturn(true);
        when(namedParameterJdbcTemplate.queryForObject(any(String.class), any(Map.class), eq(Integer.class)))
            .thenThrow(new DataAccessException("DB error") {});

        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(TEST_SCHEDULER_NAME)).thenReturn(scheduler1);

            // Act
            int count = schedulerService.getActiveInstanceCount(TEST_SCHEDULER_NAME);

            // Assert
            assertEquals(0, count); // As per current implementation, logs error and returns 0
        }
    }

    @Test
    void testGetActiveInstanceCount_SchedulerNotFound() {
        // Arrange
        String nonExistentSchedulerName = "nonExistentScheduler";
        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(nonExistentSchedulerName)).thenReturn(null);
             // Ensure schedulers list in service doesn't contain it either
            ReflectionTestUtils.setField(schedulerService, "schedulers", Collections.emptyList());


            // Act & Assert
            assertThrows(SchedulerNotFoundException.class, () -> {
                schedulerService.getActiveInstanceCount(nonExistentSchedulerName);
            });
        }
    }

    @Test
    void testGetActiveInstanceCount_SchedulerMetaDataThrowsException() throws SchedulerException {
        // Arrange
        when(scheduler1.getMetaData()).thenThrow(new SchedulerException("Meta error"));

        try (MockedStatic<SchedulerRepository> mockedStatic = mockStatic(SchedulerRepository.class)) {
            mockedStatic.when(SchedulerRepository::getInstance).thenReturn(schedulerRepository);
            when(schedulerRepository.lookup(TEST_SCHEDULER_NAME)).thenReturn(scheduler1);

            // Act & Assert
            assertThrows(SchedulerNotFoundException.class, () -> { // As per current error handling
                schedulerService.getActiveInstanceCount(TEST_SCHEDULER_NAME);
            });
        }
    }
}