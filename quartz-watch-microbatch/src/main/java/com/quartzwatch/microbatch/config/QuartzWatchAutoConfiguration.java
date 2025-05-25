package com.quartzwatch.microbatch.config;

import com.quartzwatch.api.JobRegistry;
import com.quartzwatch.api.registry.RegistryStore;
import com.quartzwatch.api.sharding.ShardingStrategy;
import com.quartzwatch.microbatch.cluster.NodeSelector;
import com.quartzwatch.microbatch.cluster.RoundRobinShardingStrategy;
import com.quartzwatch.microbatch.cluster.RandomShardingStrategy;
import com.quartzwatch.microbatch.misfire.MisfireCompensationService;
import com.quartzwatch.microbatch.misfire.MisfireJobListener;
import com.quartzwatch.microbatch.registry.DatabaseRegistryStore;
import com.quartzwatch.microbatch.registry.NodeRegistryService;
import com.quartzwatch.microbatch.service.JobRegistryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * QuartzWatch自动配置
 * 
 * @author QuartzWatch
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(QuartzWatchProperties.class)
@Import({QuartzConfiguration.class})
public class QuartzWatchAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public RegistryStore registryStore(DataSource dataSource) {
        return new DatabaseRegistryStore(dataSource);
    }
    
    @Bean
    public NodeRegistryService nodeRegistryService(RegistryStore registryStore, 
                                                   QuartzWatchProperties properties) {
        return new NodeRegistryService(registryStore, properties);
    }
    
    @Bean
    public NodeSelector nodeSelector(RegistryStore registryStore) {
        return new NodeSelector(registryStore);
    }
    
    @Bean
    public Map<String, ShardingStrategy> shardingStrategies(NodeSelector nodeSelector) {
        Map<String, ShardingStrategy> strategies = new HashMap<>();
        
        RoundRobinShardingStrategy roundRobin = new RoundRobinShardingStrategy();
        roundRobin.setNodeSelector(nodeSelector);
        strategies.put("round-robin", roundRobin);
        
        RandomShardingStrategy random = new RandomShardingStrategy();
        random.setNodeSelector(nodeSelector);
        strategies.put("random", random);
        
        return strategies;
    }
    
    @Bean
    public JobRegistry jobRegistry(NodeRegistryService nodeRegistryService,
                                   Map<String, ShardingStrategy> shardingStrategies) {
        return new JobRegistryImpl(nodeRegistryService, shardingStrategies);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public MisfireCompensationService misfireCompensationService(QuartzWatchProperties properties) {
        return new MisfireCompensationService(properties);
    }
    
    @Bean
    public MisfireJobListener misfireJobListener(MisfireCompensationService compensationService) {
        return new MisfireJobListener(compensationService);
    }
} 