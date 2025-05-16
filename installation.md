# Quartz Monitor 安装指南

## 系统要求

- JDK 8+ 
- Maven 3.6+
- Node.js 14+ (前端开发)
- MySQL 8.0+

## 后端安装

1. 编译项目：

```bash
mvn clean package
```

2. 配置数据库：

在MySQL中创建名为`quartz`的数据库：

```sql
CREATE DATABASE quartz DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

3. 配置数据库连接：

修改`src/main/resources/application.yml`配置文件中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quartz?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: your_username
    password: your_password
```

4. 运行应用：

```bash
java -jar target/quartz-monitor.jar
```

## 前端开发环境

前端代码位于`quartz-monitor-vue`目录。

1. 安装依赖：

```bash
cd quartz-monitor-vue
npm install
```

2. 启动开发服务器：

```bash
npm run serve
```

3. 构建生产版本：

```bash
npm run build
```

构建后的文件会自动部署到`src/main/resources/static`目录，这样后端应用启动时就会自动加载前端资源。

## 访问应用

启动应用后，可以通过以下URL访问：

http://localhost:8080/quartz-monitor

## 调试和排错

1. 如果遇到数据库连接问题，请检查:
   - 数据库连接参数
   - 确认MySQL服务正在运行
   - 用户权限是否正确

2. 前端API连接问题:
   - 检查代理配置
   - 检查后端服务是否正常运行

## 生产环境部署

部署到生产环境时，可以考虑以下配置：

```bash
java -jar quartz-monitor.jar --spring.profiles.active=prod
```

可以在`application-prod.yml`中配置生产环境的特定参数。
