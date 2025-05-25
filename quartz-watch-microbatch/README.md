# Quartz Watch Microbatch

分布式Quartz集群调度管理组件，提供任务分片、节点注册、Misfire补偿等功能。

## 特性

- ✅ **集群模式部署** - 支持多节点自动注册与心跳检测
- ✅ **灵活的分片策略** - 内置轮询、随机分片，支持自定义扩展
- ✅ **Misfire补偿机制** - 自动检测并补偿错过的任务执行
- ✅ **RESTful管理接口** - 提供作业管理、节点查询等HTTP接口
- ✅ **组件化设计** - 支持自定义注册存储、分片策略等扩展点
- ✅ **JMX监控支持** - 暴露关键指标供监控系统采集

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>org.wesuper.jtools</groupId>
    <artifactId>quartz-watch-microbatch</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 启用注解

在Spring Boot主类上添加`@EnableQuartzWatch`注解：

```java
@SpringBootApplication
@EnableQuartzWatch
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 配置数据源

在`application.yml`中配置数据源：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quartz_db?useSSL=false
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

# QuartzWatch配置
quartz:
  watch:
    node:
      heartbeat-interval: 5000  # 心跳间隔（毫秒）
      timeout: 30000           # 节点超时时间（毫秒）
    cluster:
      enabled: true            # 启用集群模式
      default-sharding-strategy: round-robin  # 默认分片策略
    misfire:
      enabled: true            # 启用Misfire补偿
      threshold: 30000         # 补偿阈值（毫秒）
      max-retry: 3             # 最大重试次数
      retry-interval: 5000     # 重试间隔（毫秒）
    monitor:
      jmx-enabled: true        # 启用JMX监控
      endpoint-prefix: /quartzwatch  # REST端点前缀
```

### 4. 初始化数据库

执行SQL脚本初始化数据库表：

```bash
# Quartz标准表
mysql -u root -p quartz_db < src/main/resources/quartz-tables.sql

# QuartzWatch扩展表
mysql -u root -p quartz_db < src/main/resources/quartz-watch-tables.sql
```

### 5. 创建并注册作业

```java
@Component
public class JobConfig {
    
    @Autowired
    private JobRegistry jobRegistry;
    
    @PostConstruct
    public void initJobs() {
        // 创建作业
        JobDetail jobDetail = JobBuilder.newJob(OrderProcessJob.class)
                .withIdentity("orderJob", "DEFAULT")
                .withDescription("订单处理作业")
                .build();
        
        // 创建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("orderTrigger", "DEFAULT")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
                .build();
        
        // 注册作业（10个分片）
        jobRegistry.registerJob(jobDetail, trigger, 10);
    }
}

// 作业实现
@Component
public class OrderProcessJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 获取当前节点分配的分片
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int totalShards = dataMap.getInt("totalShards");
        
        // 获取当前节点的分片信息（从注册中心获取）
        // 处理分片数据...
    }
}
```

## 扩展指南

### 实现自定义分片策略

```java
@Component
public class HashShardingStrategy implements ShardingStrategy {
    
    @Override
    public List<Shard> allocateShards(int totalShards, String jobKey) {
        // 实现基于一致性Hash的分片算法
        List<NodeInfo> nodes = getActiveNodes();
        Map<String, List<Integer>> allocation = new HashMap<>();
        
        // 分片逻辑...
        
        return buildShards(allocation);
    }
    
    @Override
    public String getName() {
        return "hash";
    }
}
```

### 实现自定义注册存储

```java
@Component
public class RedisRegistryStore implements RegistryStore {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public void registerNode(String nodeId, NodeMetadata metadata) {
        String key = "quartz:nodes:" + nodeId;
        redisTemplate.opsForValue().set(key, metadata);
        redisTemplate.expire(key, 60, TimeUnit.SECONDS);
    }
    
    @Override
    public void heartbeat(String nodeId) {
        String key = "quartz:nodes:" + nodeId;
        redisTemplate.expire(key, 60, TimeUnit.SECONDS);
    }
    
    // 其他方法实现...
}
```

### 配置示例

完整的配置示例：

```yaml
quartz:
  # Quartz原生配置
  properties:
    org.quartz.scheduler.instanceName: QuartzWatchScheduler
    org.quartz.scheduler.instanceId: AUTO
    org.quartz.threadPool.threadCount: 10
    org.quartz.jobStore.class: org.quartz.impl.jdbcjobstore.JobStoreTX
    org.quartz.jobStore.isClustered: true
    org.quartz.jobStore.clusterCheckinInterval: 20000
  
  # QuartzWatch配置
  watch:
    node:
      id: ${HOSTNAME:}:${server.port:8080}  # 自定义节点ID
      heartbeat-interval: 5000
      timeout: 30000
    cluster:
      enabled: true
      default-sharding-strategy: round-robin
    misfire:
      enabled: true
      threshold: 30000
      max-retry: 3
      retry-interval: 5000
```

## 监控端点

### REST API

| 端点 | 方法 | 描述 |
|-----|------|------|
| `/quartzwatch/registry/nodes?jobKey={jobKey}` | GET | 获取作业的节点分配信息 |
| `/quartzwatch/registry/nodes/active` | GET | 获取所有活跃节点 |
| `/quartzwatch/jobs` | GET | 获取所有作业列表 |
| `/quartzwatch/jobs/{jobKey}` | GET | 获取作业详情 |
| `/quartzwatch/jobs/{jobKey}/reschedule` | POST | 手动触发作业 |
| `/quartzwatch/jobs/{jobKey}/pause` | POST | 暂停作业 |
| `/quartzwatch/jobs/{jobKey}/resume` | POST | 恢复作业 |

### JMX指标

通过JMX暴露的监控指标：

- `QuartzWatch.registeredJobs` - 已注册的作业数量
- `QuartzWatch.activeShards` - 活跃的分片数量
- `QuartzWatch.misfireCount` - Misfire次数统计
- `QuartzWatch.nodeCount` - 集群节点数量

## 架构设计

```
quartz-watch-microbatch/
├── api/                    # 公开API
│   ├── sharding/          # 分片策略接口
│   ├── registry/          # 注册存储接口
│   ├── @EnableQuartzWatch # 自动配置注解
│   └── JobRegistry        # 作业注册门面
├── config/                # 配置类
├── controller/            # REST控制器
├── service/              # 业务服务
├── cluster/              # 集群分片实现
├── registry/             # 节点注册实现
├── misfire/              # Misfire补偿
└── dao/                  # 数据访问层
```

## 注意事项

1. **数据库要求**：需要MySQL 5.7+或兼容的数据库
2. **时钟同步**：集群节点间需要保持时钟同步
3. **网络要求**：节点间需要能够访问同一数据库
4. **分片一致性**：节点变更时会自动重新分配分片

## License

Apache License 2.0 