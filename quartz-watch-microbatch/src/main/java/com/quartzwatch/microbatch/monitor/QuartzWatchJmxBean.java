package com.quartzwatch.microbatch.monitor;

import com.quartzwatch.api.JobRegistry;
import com.quartzwatch.api.registry.RegistryStore;
import com.quartzwatch.microbatch.misfire.MisfireCompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * QuartzWatch JMX监控Bean
 * 
 * @author QuartzWatch
 */
@Component
@ManagedResource(
    objectName = "com.quartzwatch:type=QuartzWatch,name=Monitoring",
    description = "QuartzWatch monitoring metrics"
)
public class QuartzWatchJmxBean {
    
    @Autowired
    private JobRegistry jobRegistry;
    
    @Autowired
    private RegistryStore registryStore;
    
    private final AtomicLong misfireCount = new AtomicLong(0);
    private final AtomicLong compensationCount = new AtomicLong(0);
    
    @ManagedAttribute(description = "Number of registered jobs")
    public int getRegisteredJobs() {
        return jobRegistry.getAllJobs().size();
    }
    
    @ManagedAttribute(description = "Number of active nodes in cluster")
    public int getActiveNodes() {
        return registryStore.getActiveNodes().size();
    }
    
    @ManagedAttribute(description = "Total number of active shards")
    public int getActiveShards() {
        return registryStore.getActiveNodes().stream()
                .mapToInt(node -> node.getAssignedShards().length)
                .sum();
    }
    
    @ManagedAttribute(description = "Total misfire count")
    public long getMisfireCount() {
        return misfireCount.get();
    }
    
    @ManagedAttribute(description = "Total compensation count")
    public long getCompensationCount() {
        return compensationCount.get();
    }
    
    public void incrementMisfireCount() {
        misfireCount.incrementAndGet();
    }
    
    public void incrementCompensationCount() {
        compensationCount.incrementAndGet();
    }
} 