# Quartz Monitor

Quartz Monitor是一个基于Spring Boot 2和Vue.js的Quartz调度器监控和管理工具。

## 技术栈

### 后端

- Java 8
- Spring Boot 2.7.13
- Quartz 2.3.2
- MySQL 8

### 前端

- Vue.js 2.7+
- Element UI
- Axios
- Vue Router
- Vuex

## 项目结构

```
quartz-monitor/
├── src/ # 后端代码
│   ├── main/
│   │   ├── java/
│   │   │   └── com/quartz/monitor/ # 项目代码
│   │   └── resources/ # 配置文件
│   └── test/ # 测试代码
├── quartz-monitor-vue/ # 前端代码
└── pom.xml # Maven配置
```

## 功能特性

- Quartz实例管理
- 任务管理（添加、删除、暂停、恢复、执行）
- 触发器管理（添加、删除）
- Cron表达式验证工具

## 安装与运行

### 数据库准备

1. 创建MySQL数据库：`quartz`
2. 配置数据库连接：`src/main/resources/application.yml`

### 后端

```bash
# 编译打包
mvn clean package

# 运行
java -jar target/quartz-monitor.jar
```

### 前端（开发环境）

```bash
cd quartz-monitor-vue
npm install
npm run serve
```

### 前端（生产环境）

```bash
cd quartz-monitor-vue
npm install
npm run build
```

构建的结果会自动放入后端的`src/main/resources/static`目录，随后端一起部署。

## 访问应用

应用启动后，访问：http://localhost:8080/quartz-monitor

## 源代码

原始项目：https://github.com/xishuixixia/quartz-monitor
