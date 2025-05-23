<template>
  <div class="jobs-container">
    <el-card class="jobs-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <h3>任务列表</h3>
          <div class="header-actions">
            <el-button type="primary" @click="showAddJobDialog">添加任务</el-button>
            <el-button type="danger" @click="handleDelete" :disabled="!selectedJob">删除</el-button>
            <el-button type="info" @click="handlePrint">打印</el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="jobList" 
        style="width: 100%" 
        stripe 
        @row-click="handleRowClick"
        id="jobs-table"
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="jobName" label="名称" width="180" />
        <el-table-column prop="group" label="所属组" width="120" />
        <el-table-column prop="nextFireTime" label="下一次触发时间" width="180" />
        <el-table-column label="Triggers" width="100" align="center">
          <template #default="scope">
            <el-button size="small" @click.stop="showTriggers(scope.row)">
              {{ scope.row.numTriggers }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="durability" label="Durable" width="80" />
        <el-table-column prop="schedulerName" label="所属Scheduler" width="150" />
        <el-table-column label="操作" width="250" align="center">
          <template #default="scope">
            <el-button size="small" @click.stop="handleExecute(scope.row)">执行</el-button>
            <template v-if="scope.row.state === 'NORMAL'">
              <el-button size="small" type="warning" @click.stop="handlePause(scope.row)">暂停</el-button>
            </template>
            <template v-else-if="scope.row.state === 'PAUSED'">
              <el-button size="small" type="success" @click.stop="handleResume(scope.row)">恢复</el-button>
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" />
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalCount"
          :page-size="pageSize"
          :page-sizes="[20, 50, 100, 200]"
          v-model:current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加任务对话框 -->
    <el-dialog
      v-model="addJobDialogVisible"
      title="添加任务"
      width="500px"
    >
      <el-form :model="jobForm" label-width="100px" :rules="rules" ref="jobFormRef">
        <el-form-item label="job名称" prop="jobName">
          <el-input v-model="jobForm.jobName" placeholder="请输入job名称" />
        </el-form-item>
        <el-form-item label="所属组" prop="group">
          <el-input v-model="jobForm.group" placeholder="请输入组名" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="jobForm.description" type="textarea" rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="job类型" prop="jobClass">
          <el-select v-model="jobForm.jobClass" placeholder="请选择job类型" style="width: 100%">
            <el-option
              v-for="item in jobTypeOptions"
              :key="item.uuid"
              :label="item.jobName"
              :value="item.uuid"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属Schedule" prop="schedulerName">
          <el-select v-model="jobForm.schedulerName" placeholder="请选择Schedule" style="width: 100%">
            <el-option
              v-for="item in schedulerOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addJobDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitJobForm" :loading="submitting">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 触发器列表对话框 -->
    <el-dialog
      v-model="triggerDialogVisible"
      title="触发器列表"
      width="800px"
    >
      <div class="trigger-header">
        <el-button type="primary" size="small" @click="showAddTriggerDialog">添加触发器</el-button>
        <el-button type="danger" size="small" @click="handleDeleteTrigger" :disabled="!selectedTrigger">删除</el-button>
      </div>
      <el-table
        :data="triggerList"
        style="width: 100%"
        stripe
        v-loading="loadingTriggers"
        @row-click="handleTriggerRowClick"
        :row-class-name="triggerRowClassName"
      >
        <el-table-column prop="name" label="Trigger名称" width="150" align="center" />
        <el-table-column prop="group" label="Trigger组名" width="150" align="center" />
        <el-table-column prop="previousFireTime" label="上一次触发时间" width="180" />
        <el-table-column prop="nextFireTime" label="下一次触发时间" width="180" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="description" label="描述" min-width="150" align="center" />
      </el-table>
    </el-dialog>

    <!-- 添加触发器对话框 -->
    <el-dialog
      v-model="addTriggerDialogVisible"
      title="添加触发器"
      width="600px"
    >
      <!-- 触发器表单内容 -->
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { jobApi, triggerApi } from '@/api'

// 任务列表数据
const jobList = ref([])

// 触发器列表数据
const triggerList = ref([])

// 分页参数
const currentPage = ref(1)
const pageSize = ref(20)
const totalCount = ref(0)

// 选中的任务和触发器
const selectedJob = ref(null)
const selectedTrigger = ref(null)

// 对话框显示控制
const addJobDialogVisible = ref(false)
const triggerDialogVisible = ref(false)
const addTriggerDialogVisible = ref(false)

// 加载状态
const loading = ref(false)
const loadingTriggers = ref(false)
const submitting = ref(false)

// 任务表单
const jobFormRef = ref(null)
const jobForm = reactive({
  jobName: '',
  group: '',
  description: '',
  jobClass: '',
  schedulerName: ''
})

// 表单验证规则
const rules = {
  jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  group: [{ required: true, message: '请输入组名', trigger: 'blur' }],
  jobClass: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
  schedulerName: [{ required: true, message: '请选择调度器', trigger: 'change' }]
}

// 任务类型选项
const jobTypeOptions = ref([])

// 调度器选项
const schedulerOptions = ref(['QuartzScheduler', 'ReportScheduler'])

// 获取任务列表
const fetchJobList = async () => {
  loading.value = true
  try {
    const data = await jobApi.getJobs({
      pageNum: currentPage.value,
      numPerPage: pageSize.value
    })
    
    jobList.value = data.jobList
    totalCount.value = data.pageCount
  } catch (error) {
    console.error('获取任务列表失败:', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

// 获取任务类型
const fetchJobTypes = async () => {
  try {
    const data = await jobApi.getJobTypes()
    jobTypeOptions.value = data
  } catch (error) {
    console.error('获取任务类型失败:', error)
    ElMessage.error('获取任务类型失败')
  }
}

// 行样式
const tableRowClassName = ({ row }) => {
  if (selectedJob.value && selectedJob.value.uuid === row.uuid) {
    return 'selected-row'
  }
  return ''
}

const triggerRowClassName = ({ row }) => {
  if (selectedTrigger.value && selectedTrigger.value.uuid === row.uuid) {
    return 'selected-row'
  }
  return ''
}

// 处理任务行点击
const handleRowClick = (row) => {
  selectedJob.value = row
}

// 处理触发器行点击
const handleTriggerRowClick = (row) => {
  selectedTrigger.value = row
}

// 显示添加任务对话框
const showAddJobDialog = () => {
  // 重置表单
  jobForm.jobName = ''
  jobForm.group = ''
  jobForm.description = ''
  jobForm.jobClass = ''
  jobForm.schedulerName = ''
  
  // 获取任务类型
  fetchJobTypes()
  
  addJobDialogVisible.value = true
}

// 提交任务表单
const submitJobForm = async () => {
  if (!jobFormRef.value) return
  
  try {
    await jobFormRef.value.validate()
    
    submitting.value = true
    await jobApi.addJob(jobForm)
    
    ElMessage.success('添加任务成功')
    addJobDialogVisible.value = false
    
    // 重新加载任务列表
    fetchJobList()
  } catch (error) {
    console.error('添加任务失败:', error)
    ElMessage.error('添加任务失败')
  } finally {
    submitting.value = false
  }
}

// 显示触发器列表
const showTriggers = async (job) => {
  selectedJob.value = job
  triggerDialogVisible.value = true
  
  // 加载触发器列表
  loadingTriggers.value = true
  try {
    const data = await triggerApi.getTriggers(job.uuid)
    triggerList.value = data.triggerList
  } catch (error) {
    console.error('获取触发器列表失败:', error)
    ElMessage.error('获取触发器列表失败')
  } finally {
    loadingTriggers.value = false
  }
}

// 显示添加触发器对话框
const showAddTriggerDialog = () => {
  addTriggerDialogVisible.value = true
}

// 执行任务
const handleExecute = async (job) => {
  try {
    await jobApi.executeJob(job.uuid)
    ElMessage.success(`正在执行任务: ${job.jobName}`)
  } catch (error) {
    console.error('执行任务失败:', error)
    ElMessage.error('执行任务失败')
  }
}

// 暂停任务
const handlePause = async (job) => {
  try {
    await jobApi.pauseJob(job.uuid)
    ElMessage.warning(`已暂停任务: ${job.jobName}`)
    job.state = 'PAUSED'
  } catch (error) {
    console.error('暂停任务失败:', error)
    ElMessage.error('暂停任务失败')
  }
}

// 恢复任务
const handleResume = async (job) => {
  try {
    await jobApi.resumeJob(job.uuid)
    ElMessage.success(`已恢复任务: ${job.jobName}`)
    job.state = 'NORMAL'
  } catch (error) {
    console.error('恢复任务失败:', error)
    ElMessage.error('恢复任务失败')
  }
}

// 删除任务
const handleDelete = () => {
  if (!selectedJob.value) {
    ElMessage.warning('请先选择要删除的任务')
    return
  }
  
  ElMessageBox.confirm(`确定要删除任务 ${selectedJob.value.jobName} 吗?`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await jobApi.deleteJob(selectedJob.value.uuid)
      ElMessage.success(`已删除任务: ${selectedJob.value.jobName}`)
      
      // 重新加载任务列表
      fetchJobList()
      
      // 清空选中的任务
      selectedJob.value = null
    } catch (error) {
      console.error('删除任务失败:', error)
      ElMessage.error('删除任务失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 删除触发器
const handleDeleteTrigger = () => {
  if (!selectedTrigger.value) {
    ElMessage.warning('请先选择要删除的触发器')
    return
  }
  
  ElMessageBox.confirm(`确定要删除触发器 ${selectedTrigger.value.name} 吗?`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await triggerApi.deleteTrigger(selectedTrigger.value.uuid)
      ElMessage.success(`已删除触发器: ${selectedTrigger.value.name}`)
      
      // 从列表移除已删除的触发器
      triggerList.value = triggerList.value.filter(item => item.uuid !== selectedTrigger.value.uuid)
      
      // 清空选中的触发器
      selectedTrigger.value = null
    } catch (error) {
      console.error('删除触发器失败:', error)
      ElMessage.error('删除触发器失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 打印
const handlePrint = () => {
  window.print()
}

// 页面大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  // 重新加载任务列表
  fetchJobList()
}

// 页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  // 重新加载任务列表
  fetchJobList()
}

onMounted(() => {
  // 加载任务列表
  fetchJobList()
})
</script>

<style scoped>
.jobs-container {
  padding: 10px;
}

.jobs-card {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.selected-row {
  background-color: #f0f9eb;
}

.trigger-header {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}
</style> 