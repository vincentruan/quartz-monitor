# Quartz Monitor API 文档

## 基础信息

- 所有API请求均使用JSON格式
- 所有API响应格式为
  ```json
  {
    "code": 200,       // 状态码
    "message": "成功",  // 状态信息
    "data": { ... }    // 响应数据（可选）
  }
  ```
- 请求失败时状态码不为200，并且message包含错误信息

## 配置管理 API

### 获取配置列表

- **URL**: `/api/config/list`
- **方法**: GET
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": [
      {
        "uuid": "1234-5678-90ab",
        "container": "weblogic",
        "name": "localhost@9010",
        "host": "localhost",
        "port": 9010,
        "userName": "admin",
        "password": "******"
      }
    ]
  }
  ```

### 获取配置详情

- **URL**: `/api/config/{uuid}`
- **方法**: GET
- **参数**: 
  - `uuid`: 配置ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "uuid": "1234-5678-90ab",
      "container": "weblogic",
      "name": "localhost@9010",
      "host": "localhost",
      "port": 9010,
      "userName": "admin",
      "password": "******"
    }
  }
  ```

### 添加配置

- **URL**: `/api/config/add`
- **方法**: POST
- **请求体**:
  ```json
  {
    "container": "weblogic",
    "host": "localhost",
    "port": 9010,
    "userName": "admin",
    "password": "password"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "添加成功",
    "data": {
      "uuid": "1234-5678-90ab",
      "container": "weblogic",
      "name": "localhost@9010",
      "host": "localhost",
      "port": 9010,
      "userName": "admin",
      "password": "******"
    }
  }
  ```

### 更新配置

- **URL**: `/api/config/{uuid}`
- **方法**: PUT
- **参数**: 
  - `uuid`: 配置ID
- **请求体**:
  ```json
  {
    "container": "weblogic",
    "host": "localhost",
    "port": 9010,
    "userName": "admin",
    "password": "password"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "uuid": "1234-5678-90ab",
      "container": "weblogic",
      "name": "localhost@9010",
      "host": "localhost",
      "port": 9010,
      "userName": "admin",
      "password": "******"
    }
  }
  ```

### 删除配置

- **URL**: `/api/config/{uuid}`
- **方法**: DELETE
- **参数**: 
  - `uuid`: 配置ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 测试连接

- **URL**: `/api/config/test`
- **方法**: POST
- **请求体**:
  ```json
  {
    "container": "weblogic",
    "host": "localhost",
    "port": 9010,
    "userName": "admin",
    "password": "password"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": {
      "schedulers": 2,
      "message": "连接成功"
    }
  }
  ```

## 任务管理 API

### 获取任务列表

- **URL**: `/api/job/list`
- **方法**: GET
- **参数**:
  - `pageNum`: 页码，默认1
  - `numPerPage`: 每页记录数，默认20
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "uuid": "abcd-1234-5678",
        "schedulerName": "QuartzScheduler",
        "quartzInstanceId": "1234-5678-90ab",
        "schedulerInstanceId": "node1",
        "jobName": "DemoJob",
        "group": "DEFAULT",
        "jobClass": "org.example.DemoJob",
        "description": "示例任务",
        "state": "NORMAL",
        "durability": true,
        "shouldRecover": false,
        "nextFireTime": "2023-06-01T12:00:00"
      }
    ],
    "total": 10,
    "pageNum": 1,
    "pageCount": 1
  }
  ```

### 执行任务

- **URL**: `/api/job/start`
- **方法**: POST
- **参数**:
  - `uuid`: 任务ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "执行成功"
  }
  ```

### 暂停任务

- **URL**: `/api/job/pause`
- **方法**: POST
- **参数**:
  - `uuid`: 任务ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "任务已暂停"
  }
  ```

### 恢复任务

- **URL**: `/api/job/resume`
- **方法**: POST
- **参数**:
  - `uuid`: 任务ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "任务已恢复"
  }
  ```

### 删除任务

- **URL**: `/api/job/delete`
- **方法**: DELETE
- **参数**:
  - `uuid`: 任务ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 获取可用的调度器列表

- **URL**: `/api/job/types`
- **方法**: GET
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": {
      "schedulers": ["QuartzScheduler", "AnotherScheduler"],
      "jobs": { ... }
    }
  }
  ```

### 添加任务

- **URL**: `/api/job/add`
- **方法**: POST
- **请求体**:
  ```json
  {
    "jobName": "NewJob",
    "group": "DEFAULT",
    "description": "新任务",
    "jobClass": "org.example.job.MyJob",
    "schedulerName": "QuartzScheduler"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "添加成功"
  }
  ```

## 触发器管理 API

### 获取触发器列表

- **URL**: `/api/trigger/list`
- **方法**: GET
- **参数**:
  - `jobId`: 任务ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "uuid": "trig-1234-5678",
        "jobId": "abcd-1234-5678",
        "name": "DemoTrigger",
        "group": "DEFAULT",
        "jobName": "DemoJob",
        "jobGroup": "DEFAULT",
        "description": "示例触发器",
        "nextFireTime": "2023-06-01T12:00:00",
        "previousFireTime": "2023-06-01T11:00:00",
        "startTime": "2023-01-01T00:00:00",
        "endTime": null,
        "misfireInstruction": 0,
        "priority": 5,
        "calendarName": null,
        "STriggerState": "NORMAL"
      }
    ]
  }
  ```

### 添加触发器

- **URL**: `/api/trigger/add`
- **方法**: POST
- **请求体**:
  ```json
  {
    "jobId": "abcd-1234-5678",
    "name": "NewTrigger",
    "group": "DEFAULT",
    "description": "新触发器",
    "dateFlag": 0,
    "cron": "0 0 12 * * ?"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "添加成功"
  }
  ```

### 删除触发器

- **URL**: `/api/trigger/delete`
- **方法**: DELETE
- **参数**:
  - `uuid`: 触发器ID
- **响应示例**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

## Cron表达式 API

### 解析Cron表达式

- **URL**: `/api/json/parseCronExp`
- **方法**: POST
- **请求体**:
  ```json
  {
    "cronExpression": "0 0 12 * * ?"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": {
      "secLabel": "0",
      "minLabel": "0",
      "hhLabel": "12",
      "dayLabel": "*",
      "monthLabel": "*",
      "weekLabel": "?",
      "yearLabel": "*",
      "startDate": "2023-06-01 12:00:00",
      "schedulerNextResults": [
        "2023-06-01 12:00:00",
        "2023-06-02 12:00:00",
        "2023-06-03 12:00:00",
        "2023-06-04 12:00:00",
        "2023-06-05 12:00:00",
        "2023-06-06 12:00:00",
        "2023-06-07 12:00:00",
        "2023-06-08 12:00:00"
      ]
    }
  }
  ```

### 生成Cron表达式

- **URL**: `/api/json/generateCronExp`
- **方法**: POST
- **请求体**:
  ```json
  {
    "minuteexptype": "1",
    "assignMins": [0, 15, 30, 45],
    "hourexptype": "1",
    "assignHours": [9, 12, 18],
    "useweekck": "0",
    "dayexptype": "0",
    "monthexptype": "0",
    "useyearck": "0"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": {
      "cronExpression": "0 0,15,30,45 9,12,18 * * ?",
      "startDate": "2023-06-01 09:00:00",
      "schedulerNextResults": [
        "2023-06-01 09:00:00",
        "2023-06-01 09:15:00",
        "2023-06-01 09:30:00",
        "2023-06-01 09:45:00",
        "2023-06-01 12:00:00",
        "2023-06-01 12:15:00",
        "2023-06-01 12:30:00",
        "2023-06-01 12:45:00"
      ]
    }
  }
  ```

## 仪表盘 API

### 获取系统统计信息

- **URL**: `/api/dashboard/stats`
- **方法**: GET
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": {
      "instanceCount": 2,
      "schedulerCount": 4,
      "jobCount": 20,
      "triggerCount": 32,
      "runningJobCount": 15
    }
  }
  ```

### 获取最近执行的任务

- **URL**: `/api/dashboard/recent-jobs`
- **方法**: GET
- **响应示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "uuid": "abcd-1234-5678",
        "schedulerName": "QuartzScheduler",
        "jobName": "DemoJob",
        "group": "DEFAULT",
        "description": "示例任务",
        "state": "NORMAL",
        "nextFireTime": "2023-06-01T12:00:00"
      },
      // 更多任务...
    ]
  }
  ``` 