# Quartz Monitor Vue 前端

这是Quartz Monitor的Vue前端项目，基于Vue 2.x和Element UI构建。

## 项目结构

```
quartz-monitor-vue/
├── public/ # 静态资源
├── src/
│   ├── api/ # API接口
│   ├── assets/ # 静态资源
│   ├── components/ # 通用组件
│   ├── router/ # 路由配置
│   ├── store/ # Vuex状态管理
│   ├── views/ # 页面视图
│   ├── App.vue # 根组件
│   └── main.js # 入口文件
└── package.json # 项目依赖
```

## 功能特性

- 任务列表显示
- 任务添加/修改/删除
- 触发器管理
- Cron表达式验证与生成

## 开发环境设置

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 构建生产版本
npm run build
```

## 生产部署

构建完成后，将`dist`目录下的文件复制到Spring Boot项目的`src/main/resources/static`目录中。

## 接口说明

与Spring Boot后端通过以下API接口进行通信：

- `/api/job/*` - 任务管理相关接口
- `/api/trigger/*` - 触发器管理相关接口 