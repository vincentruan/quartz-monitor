import axios from 'axios'

// 创建 axios 实例
const api = axios.create({
  baseURL: '/api', // 根据实际部署情况调整
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里处理请求头等
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果是Cron API的特殊响应格式
    if (res.result && res.result.statusCode) {
      if (res.result.statusCode !== "200") {
        console.error('Cron API错误:', res.result.message || '未知错误')
        return Promise.reject(new Error(res.result.message || '未知错误'))
      }
      return res // 返回原始响应数据
    }
    
    // 如果响应状态码不是200，抛出错误
    if (res.code !== 200) {
      console.error('API错误:', res.message || '未知错误')
      return Promise.reject(new Error(res.message || '未知错误'))
    }
    
    // 直接返回数据部分
    return res.data
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// Job 相关接口
export const jobApi = {
  // 获取任务列表
  getJobs(params) {
    return api.get('/job/list', { params })
  },
  
  // 添加任务
  addJob(data) {
    return api.post('/job/add', data)
  },
  
  // 删除任务
  deleteJob(uuid) {
    return api.delete(`/job/delete?uuid=${uuid}`)
  },
  
  // 执行任务
  executeJob(uuid) {
    return api.post(`/job/start?uuid=${uuid}`)
  },
  
  // 暂停任务
  pauseJob(uuid) {
    return api.post(`/job/pause?uuid=${uuid}`)
  },
  
  // 恢复任务
  resumeJob(uuid) {
    return api.post(`/job/resume?uuid=${uuid}`)
  },
  
  // 获取可用的 Job 类型
  getJobTypes() {
    return api.get('/job/types')
  }
}

// Trigger 相关接口
export const triggerApi = {
  // 获取触发器列表
  getTriggers(jobId) {
    return api.get(`/trigger/list?jobId=${jobId}`)
  },
  
  // 添加触发器
  addTrigger(data) {
    return api.post('/trigger/add', data)
  },
  
  // 删除触发器
  deleteTrigger(uuid) {
    return api.delete(`/trigger/delete?uuid=${uuid}`)
  }
}

// Cron 表达式相关接口
export const cronApi = {
  // 验证 Cron 表达式
  validateCronExpression(cronExpression) {
    return api.post('/json/parseCronExp', { cronExpression })
  },
  
  // 生成 Cron 表达式
  generateCronExpression(data) {
    return api.post('/json/generateCronExp', data)
  }
}

// 仪表盘相关接口
export const dashboardApi = {
  // 获取统计信息
  getStats() {
    return api.get('/dashboard/stats')
  },
  
  // 获取最近执行的任务
  getRecentJobs() {
    return api.get('/dashboard/recent-jobs')
  }
}

export default {
  jobApi,
  triggerApi,
  cronApi,
  dashboardApi
} 