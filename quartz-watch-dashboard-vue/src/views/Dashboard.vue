<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h3>欢迎使用Quartz监控工具</h3>
        </div>
      </template>
      <div class="card-content">
        <p class="dashboard-info">Quartz Monitor是一个用于监控和管理Quartz调度器的工具。</p>
        <p class="dashboard-info">官方微博: <a href="http://weibo.com/xishuixixia" target="_blank">http://weibo.com/xishuixixia</a></p>
      </div>
    </el-card>

    <el-row :gutter="20" class="data-cards">
      <el-col :span="8">
        <el-card class="data-card" v-loading="loading">
          <h4>任务数量</h4>
          <div class="data-value">{{ stats.jobCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="data-card" v-loading="loading">
          <h4>触发器数量</h4>
          <div class="data-value">{{ stats.triggerCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="data-card" v-loading="loading">
          <h4>调度器状态</h4>
          <div class="data-value">{{ stats.schedulerStatus || '未知' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-jobs" v-loading="loadingJobs">
      <template #header>
        <div class="card-header">
          <h3>最近执行的任务</h3>
        </div>
      </template>
      <el-table :data="recentJobs" stripe style="width: 100%">
        <el-table-column prop="jobName" label="任务名称" />
        <el-table-column prop="group" label="所属组" />
        <el-table-column prop="fireTime" label="触发时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { dashboardApi } from '@/api'
import { ElMessage } from 'element-plus'

// 仪表盘数据
const stats = ref({
  jobCount: 0,
  triggerCount: 0,
  schedulerStatus: '未知'
})

const recentJobs = ref([])

// 加载状态
const loading = ref(false)
const loadingJobs = ref(false)

// 获取仪表盘统计数据
const fetchStats = async () => {
  loading.value = true
  try {
    const data = await dashboardApi.getStats()
    stats.value = data
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

// 获取最近执行的任务
const fetchRecentJobs = async () => {
  loadingJobs.value = true
  try {
    const data = await dashboardApi.getRecentJobs()
    recentJobs.value = data
  } catch (error) {
    console.error('获取最近执行任务失败:', error)
    ElMessage.error('获取最近执行任务失败')
  } finally {
    loadingJobs.value = false
  }
}

const getStatusType = (status) => {
  switch (status) {
    case '成功':
      return 'success'
    case '失败':
      return 'danger'
    case '运行中':
      return 'warning'
    default:
      return 'info'
  }
}

onMounted(async () => {
  try {
    // 加载实际数据
    const statsData = await dashboardApi.getStats()
    stats.value = statsData
    
    const jobsData = await dashboardApi.getRecentJobs()
    recentJobs.value = jobsData
    
    console.log('Dashboard数据加载成功:', { stats: statsData, jobs: jobsData })
  } catch (error) {
    console.error('Dashboard数据加载失败:', error)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.card-content {
  padding: 10px 0;
}

.dashboard-info {
  line-height: 24px;
  margin: 5px 0;
  color: #606266;
}

.data-cards {
  margin-bottom: 20px;
}

.data-card {
  text-align: center;
  height: 120px;
}

.data-card h4 {
  margin: 0 0 15px;
  color: #909399;
  font-weight: normal;
}

.data-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.recent-jobs {
  margin-bottom: 20px;
}
</style> 