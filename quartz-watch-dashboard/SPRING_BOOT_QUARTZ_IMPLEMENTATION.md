# Spring Boot Quartz 实现说明

## 概述

本文档说明了如何使用 Spring Boot Quartz 组件替代原有的容器类实现来获取 Dashboard 统计信息。

## 主要变更

### 1. DashboardController (基础版本)

位置：`com.quartz.monitor.controller.DashboardController`

主要特点：
- 使用 Spring Boot 自动注入的 `Scheduler` 实例
- 通过 Quartz API 直接获取作业、触发器和运行状态信息
- 适用于单个 Scheduler 实例的应用

### 2. EnhancedDashboardController (增强版本)

位置：`com.quartz.monitor.controller.EnhancedDashboardController`

主要特点：
- 支持多个 Scheduler 实例
- 使用 `SchedulerRepository` 查找所有注册的 Scheduler
- 聚合所有 Scheduler 的统计信息
- API 路径：`/api/dashboard/v2/*`

## 使用方法

### 1. 基础版本使用

```yaml
# application.yml
spring:
  quartz:
    job-store-type: jdbc
    jdbc:
      initialize-schema: always
    properties:
      org.quartz.scheduler.instanceName: DefaultScheduler
      org.quartz.scheduler.instanceId: AUTO
```

访问 API：
- 获取统计信息：`GET /api/dashboard/stats`
- 获取最近作业：`GET /api/dashboard/recent-jobs`

### 2. 增强版本使用

如果需要支持多个 Scheduler 实例，使用增强版本：

访问 API：
- 获取统计信息：`GET /api/dashboard/v2/stats`
- 获取最近作业：`GET /api/dashboard/v2/recent-jobs`

## Quartz 配置

可选配置类：`com.quartz.monitor.config.QuartzConfig`

主要配置项：
- 数据源配置（用于持久化作业）
- 实例名称和 ID
- 线程池大小
- 集群配置

## 主要 API 对比

### 原实现
```java
// 使用容器类
QuartzInstanceContainer.getAllQuartzConfigs()
JobContainer.getJobMap()
TriggerContainer.getTriggerCount()
```

### 新实现
```java
// 使用 Spring Boot Quartz API
scheduler.getJobGroupNames()
scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))
scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName))
scheduler.getCurrentlyExecutingJobs()
```

## 优势

1. **标准化**：使用 Spring Boot 和 Quartz 的标准 API
2. **简化依赖**：不需要自定义容器类
3. **更好的集成**：与 Spring Boot 生态系统无缝集成
4. **灵活性**：支持单个或多个 Scheduler 实例
5. **可维护性**：使用标准 API，易于理解和维护

## 注意事项

1. Job DTO 类没有 `previousFireTime` 字段，如需要可以添加
2. 默认实现假设使用单个 Scheduler 实例
3. 如果使用集群模式，确保正确配置数据源和集群参数

## 迁移建议

1. 先在测试环境验证新实现
2. 可以同时保留两个版本的 API，逐步迁移
3. 监控性能差异，特别是在大量作业的情况下
4. 确保前端调用的 API 路径正确 