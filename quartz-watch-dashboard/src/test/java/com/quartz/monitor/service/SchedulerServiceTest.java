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
import org.quartz.impl.SchedulerRepository;
import org.springframework.test.util.ReflectionTestUtils;

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
    
    @InjectMocks
    private SchedulerService schedulerService;
    
    @BeforeEach
    void setUp() throws SchedulerException {
        // Setup default scheduler
        when(defaultScheduler.getSchedulerName()).thenReturn("defaultScheduler");
        when(defaultScheduler.isStarted()).thenReturn(true);
        
        // Setup additional schedulers
        when(scheduler1.getSchedulerName()).thenReturn("scheduler1");
        when(scheduler1.isStarted()).thenReturn(true);
        
        when(scheduler2.getSchedulerName()).thenReturn("scheduler2");
        when(scheduler2.isStarted()).thenReturn(false);
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
} 