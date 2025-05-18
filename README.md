# Quartz Monitor

Quartz Monitor 是一个强大的 Quartz 任务调度监控和管理系统，基于 Spring Boot 和 Vue 技术栈。该系统允许用户通过直观的界面来管理和监控 Quartz 任务调度器。

## 功能特点

- 多 Quartz 实例支持：可同时连接和管理多个 Quartz 实例
- 任务管理：查看、添加、删除、暂停和恢复 Quartz 任务
- 触发器管理：为任务创建和管理不同类型的触发器
- Cron 表达式工具：验证和生成 Cron 表达式
- 实时监控：查看任务执行状态和调度器统计信息
- RESTful API：提供完整的 API 用于与其他系统集成

## 系统架构

- 后端：Spring Boot 2.7.x，JDK 8+
- 前端：Vue3，Vite
- 数据库：MySQL

## 环境要求

- JDK 8 或更高版本
- Maven 3.6+
- Node.js 14+
- MySQL 5.7+

## 快速开始

### 数据库配置

1. 创建数据库和用户

```sql
CREATE DATABASE app_quartz_monitor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'app_quartz_monitor'@'%' IDENTIFIED BY 'YourPassword';
GRANT ALL PRIVILEGES ON app_quartz_monitor.* TO 'app_quartz_monitor'@'%';
FLUSH PRIVILEGES;
```

2. 导入数据表结构

```sql
USE app_quartz_monitor;
SOURCE docs/sql/schema.sql;
```

### 后端部署

1. 修改配置文件

编辑 `src/main/resources/application.yml` 文件，设置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-database-host:3306/app_quartz_monitor?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: app_quartz_monitor
    password: YourPassword
```

2. 编译打包

```bash
mvn clean package -DskipTests
```

3. 运行

```bash
java -jar target/quartz-monitor.jar
```

### 前端部署

1. 安装依赖

```bash
cd quartz-monitor-vue
npm install
```

2. 开发模式

```bash
npm run dev
```

3. 打包生产环境

```bash
npm run build
```

生成的文件将位于 `quartz-monitor-vue/dist` 目录，可以部署到任何Web服务器。

## API 接口文档

详细的 API 接口文档请参考 `docs/api.md` 文件。

### 主要 API 端点

- 查询接口：
  - 获取任务列表: `GET /api/job/list`
  - 获取触发器列表: `GET /api/trigger/list`
  - 获取系统统计: `GET /api/dashboard/stats`

- 操作接口：
  - 添加任务: `POST /api/job/add`
  - 添加触发器: `POST /api/trigger/add`
  - 执行任务: `POST /api/job/start`
  - 暂停任务: `POST /api/job/pause`
  - 恢复任务: `POST /api/job/resume`

## 配置远程 Quartz 实例

1. 添加 JMX 配置到 Quartz 实例

要监控远程 Quartz 实例，需要在目标实例上启用 JMX：

```properties
# 在 Quartz 配置文件中添加如下配置
org.quartz.scheduler.jmx.export=true
```

2. 在监控系统中添加实例

通过 Web 界面或 API 添加实例：

```
POST /api/config/add
{
  "container": "weblogic", 
  "host": "your-quartz-host",
  "port": 9010,
  "userName": "jmx-username",
  "password": "jmx-password"
}
```

## 贡献

欢迎提交 Issue 和 Pull Request 来帮助改进这个项目。

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。
