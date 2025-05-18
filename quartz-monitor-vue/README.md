# Quartz Monitor Vue 前端

这是Quartz Monitor的Vue前端项目，基于Vue 3.x和Element Plus构建。

## 项目结构

```
quartz-monitor-vue/
├── public/           # 静态资源
├── src/
│   ├── api/          # API接口
│   ├── assets/       # 静态资源
│   ├── components/   # 通用组件
│   ├── router/       # 路由配置
│   ├── store/        # 状态管理
│   ├── views/        # 页面视图
│   │   ├── Dashboard.vue       # 仪表盘/主页
│   │   ├── Jobs.vue           # 任务列表
│   │   ├── CronExpression.vue # Cron表达式验证
│   │   └── Layout.vue         # 布局组件
│   ├── utils/        # 工具函数
│   ├── App.vue       # 根组件
│   └── main.js       # 入口文件
└── package.json      # 项目依赖
```

## 功能特性

- 任务列表显示与管理
- 任务添加/修改/删除
- 触发器管理
- Cron表达式验证与生成
- 响应式界面设计

## 开发环境设置

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 生产部署

构建完成后，将`dist`目录下的文件复制到后端项目的静态资源目录。

## 技术栈

- Vue 3.x - 渐进式JavaScript框架
- Vue Router 4.x - Vue路由管理
- Element Plus - UI组件库
- Axios - HTTP客户端
- Vite - 前端构建工具

## 与后端集成

前端通过RESTful API与后端通信，API路径配置在`src/api/index.js`文件中。在开发环境中，API请求被代理到本地后端服务（在`vite.config.js`中配置）。

## 接口说明

与Spring Boot后端通过以下API接口进行通信：

- `/api/job/*` - 任务管理相关接口
- `/api/trigger/*` - 触发器管理相关接口 