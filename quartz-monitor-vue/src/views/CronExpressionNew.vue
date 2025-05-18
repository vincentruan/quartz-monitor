<template>
  <div class="cron-container">
    <el-card class="cron-card">
      <template #header>
        <div class="card-header">
          <h3>Cron表达式验证工具</h3>
        </div>
      </template>

      <div class="cron-input-section">
        <el-form :model="cronForm" label-width="120px">
          <el-form-item label="Cron表达式">
            <div class="cron-input-group">
              <el-input 
                v-model="cronForm.expression" 
                placeholder="请输入cron表达式 (例如: 0 0 12 * * ?)"
                @keyup.enter="validateCronExpression" 
              />
              <el-button type="primary" @click="validateCronExpression" :loading="validating">验证</el-button>
              <el-button type="success" @click="copyToClipboard">复制表达式</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <el-tabs v-model="activeTab" class="cron-tabs">
        <el-tab-pane label="表达式详解" name="explanation">
          <el-form :model="cronParts" label-width="120px" class="cron-parts" v-loading="validating">
            <el-form-item label="秒">
              <el-input v-model="cronParts.secLabel" readonly />
            </el-form-item>
            <el-form-item label="分">
              <el-input v-model="cronParts.minLabel" readonly />
            </el-form-item>
            <el-form-item label="时">
              <el-input v-model="cronParts.hhLabel" readonly />
            </el-form-item>
            <el-form-item label="日">
              <el-input v-model="cronParts.dayLabel" readonly />
            </el-form-item>
            <el-form-item label="月">
              <el-input v-model="cronParts.monthLabel" readonly />
            </el-form-item>
            <el-form-item label="周">
              <el-input v-model="cronParts.weekLabel" readonly />
            </el-form-item>
            <el-form-item label="年">
              <el-input v-model="cronParts.yearLabel" readonly />
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="cronParts.startDate"
                type="datetime"
                placeholder="请选择开始日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="下次执行时间" name="nextFireTimes">
          <el-table :data="nextFireTimes" stripe style="width: 100%" v-loading="validating">
            <el-table-column type="index" label="序号" width="80" align="center" />
            <el-table-column prop="time" label="执行时间" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="表达式生成器" name="generator">
          <el-form :model="generatorForm" label-width="100px" v-loading="generating">
            <el-divider content-position="left">秒</el-divider>
            <el-form-item>
              <el-radio v-model="generatorForm.secType" label="0">每秒</el-radio>
              <el-radio v-model="generatorForm.secType" label="1">周期</el-radio>
            </el-form-item>
            
            <template v-if="generatorForm.secType === '1'">
              <el-form-item label="周期范围">
                <div class="cycle-range">
                  <el-input-number v-model="generatorForm.secStart" :min="0" :max="59" />
                  <span class="range-separator">-</span>
                  <el-input-number v-model="generatorForm.secEnd" :min="0" :max="59" />
                </div>
              </el-form-item>
            </template>

            <el-divider content-position="left">分钟</el-divider>
            <el-form-item>
              <el-radio v-model="generatorForm.minType" label="0">周期</el-radio>
              <el-radio v-model="generatorForm.minType" label="1">指定</el-radio>
            </el-form-item>
            
            <template v-if="generatorForm.minType === '0'">
              <el-form-item label="周期范围">
                <div class="flex-row">
                  从 <el-input-number v-model="generatorForm.startMinute" :min="0" :max="59" />
                  开始，每 <el-input-number v-model="generatorForm.everyMinute" :min="1" :max="59" /> 分钟执行一次
                </div>
              </el-form-item>
            </template>
            
            <template v-if="generatorForm.minType === '1'">
              <el-form-item label="指定分钟">
                <div class="checkbox-group">
                  <el-checkbox-group v-model="generatorForm.assignMins">
                    <el-checkbox 
                      v-for="n in 60" 
                      :key="`min-${n-1}`" 
                      :label="n-1"
                    >
                      {{ n-1 }}
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-form-item>
            </template>

            <el-divider content-position="left">小时</el-divider>
            <el-form-item>
              <el-radio v-model="generatorForm.hourType" label="0">每小时</el-radio>
              <el-radio v-model="generatorForm.hourType" label="1">指定</el-radio>
            </el-form-item>
            
            <template v-if="generatorForm.hourType === '1'">
              <el-form-item label="指定小时">
                <div class="checkbox-group">
                  <el-checkbox-group v-model="generatorForm.assignHours">
                    <el-checkbox 
                      v-for="n in 24" 
                      :key="`hour-${n-1}`" 
                      :label="n-1"
                    >
                      {{ n-1 }}
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-form-item>
            </template>

            <el-divider content-position="left">日期</el-divider>
            <el-form-item>
              <el-radio v-model="generatorForm.dayType" label="0">每日</el-radio>
              <el-radio v-model="generatorForm.dayType" label="1">指定</el-radio>
            </el-form-item>
            
            <template v-if="generatorForm.dayType === '1'">
              <el-form-item label="指定日期">
                <div class="checkbox-group">
                  <el-checkbox-group v-model="generatorForm.assignDays">
                    <el-checkbox 
                      v-for="n in 31" 
                      :key="`day-${n}`" 
                      :label="n"
                    >
                      {{ n }}
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-form-item>
            </template>

            <el-divider content-position="left">月份</el-divider>
            <el-form-item>
              <el-radio v-model="generatorForm.monthType" label="0">每月</el-radio>
              <el-radio v-model="generatorForm.monthType" label="1">指定</el-radio>
            </el-form-item>
            
            <template v-if="generatorForm.monthType === '1'">
              <el-form-item label="指定月份">
                <div class="checkbox-group">
                  <el-checkbox-group v-model="generatorForm.assignMonths">
                    <el-checkbox 
                      v-for="n in 12" 
                      :key="`month-${n}`" 
                      :label="n"
                    >
                      {{ n }}月
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </el-form-item>
            </template>

            <el-divider content-position="left">星期</el-divider>
            <el-form-item>
              <el-checkbox v-model="generatorForm.useWeek">指定星期</el-checkbox>
            </el-form-item>
            
            <template v-if="generatorForm.useWeek">
              <el-form-item>
                <el-radio v-model="generatorForm.weekType" label="0">每周</el-radio>
                <el-radio v-model="generatorForm.weekType" label="1">指定</el-radio>
              </el-form-item>
              
              <template v-if="generatorForm.weekType === '1'">
                <el-form-item label="指定星期">
                  <div class="checkbox-group">
                    <el-checkbox-group v-model="generatorForm.assignWeeks">
                      <el-checkbox label="1">星期日</el-checkbox>
                      <el-checkbox label="2">星期一</el-checkbox>
                      <el-checkbox label="3">星期二</el-checkbox>
                      <el-checkbox label="4">星期三</el-checkbox>
                      <el-checkbox label="5">星期四</el-checkbox>
                      <el-checkbox label="6">星期五</el-checkbox>
                      <el-checkbox label="7">星期六</el-checkbox>
                    </el-checkbox-group>
                  </div>
                </el-form-item>
              </template>
            </template>

            <el-divider content-position="left">年份</el-divider>
            <el-form-item>
              <el-checkbox v-model="generatorForm.useYear">指定年份</el-checkbox>
            </el-form-item>
            
            <template v-if="generatorForm.useYear">
              <el-form-item>
                <el-radio v-model="generatorForm.yearType" label="0">不指定</el-radio>
                <el-radio v-model="generatorForm.yearType" label="1">指定</el-radio>
              </el-form-item>
              
              <template v-if="generatorForm.yearType === '1'">
                <el-form-item label="年份表达式">
                  <el-input v-model="generatorForm.yearExp" placeholder="例如: 2023-2025" />
                </el-form-item>
              </template>
            </template>

            <el-divider></el-divider>
            <el-form-item>
              <el-button type="primary" @click="generateCronExpression" :loading="generating">生成表达式</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { cronApi } from '@/api'

// 活动标签页
const activeTab = ref('explanation')

// 加载状态
const validating = ref(false)
const generating = ref(false)

// Cron表达式表单
const cronForm = reactive({
  expression: ''
})

// Cron表达式解析结果
const cronParts = reactive({
  secLabel: '',
  minLabel: '',
  hhLabel: '',
  dayLabel: '',
  monthLabel: '',
  weekLabel: '',
  yearLabel: '',
  startDate: new Date()
})

// 下次执行时间
const nextFireTimes = ref([])

// 表达式生成器表单
const generatorForm = reactive({
  secType: '0',
  secStart: 0,
  secEnd: 59,
  
  minType: '0',
  startMinute: 0,
  everyMinute: 1,
  assignMins: [],
  
  hourType: '0',
  assignHours: [],
  
  dayType: '0',
  assignDays: [],
  
  monthType: '0',
  assignMonths: [],
  
  useWeek: false,
  weekType: '0',
  assignWeeks: [],
  
  useYear: false,
  yearType: '0',
  yearExp: ''
})

// 验证Cron表达式
const validateCronExpression = async () => {
  if (!cronForm.expression) {
    ElMessage.warning('请输入Cron表达式')
    return
  }

  validating.value = true
  try {
    const response = await cronApi.validateCronExpression(cronForm.expression)
    
    // 更新解析结果
    cronParts.secLabel = response.secLabel
    cronParts.minLabel = response.minLabel
    cronParts.hhLabel = response.hhLabel
    cronParts.dayLabel = response.dayLabel
    cronParts.monthLabel = response.monthLabel
    cronParts.weekLabel = response.weekLabel
    cronParts.yearLabel = response.yearLabel
    cronParts.startDate = response.startDate
    
    // 更新下次执行时间
    nextFireTimes.value = response.schedulerNextResults.map(time => ({ time }))
    
    ElMessage.success('Cron表达式验证成功')
  } catch (error) {
    console.error('验证Cron表达式失败:', error)
    ElMessage.error(error.message || 'Cron表达式验证失败')
  } finally {
    validating.value = false
  }
}

// 复制表达式到剪贴板
const copyToClipboard = () => {
  if (!cronForm.expression) {
    ElMessage.warning('请先输入Cron表达式')
    return
  }
  
  navigator.clipboard.writeText(cronForm.expression)
    .then(() => {
      ElMessage.success('已复制到剪贴板')
    })
    .catch(() => {
      ElMessage.error('复制失败')
    })
}

// 生成Cron表达式
const generateCronExpression = async () => {
  generating.value = true
  
  try {
    // 收集表单数据
    const formData = {
      cycle_exp_min_type: generatorForm.minType === '0' ? 'on' : '',
      assign_exp_min_type: generatorForm.minType === '1' ? 'on' : '',
      startMinute: generatorForm.startMinute,
      everyMinute: generatorForm.everyMinute,
      assignMins: generatorForm.assignMins,
      
      exp_hh_type_0: generatorForm.hourType === '0' ? 'on' : '',
      exp_hh_type_1: generatorForm.hourType === '1' ? 'on' : '',
      assignHours: generatorForm.assignHours,
      
      exp_day_type_0: generatorForm.dayType === '0' ? 'on' : '',
      exp_day_type_1: generatorForm.dayType === '1' ? 'on' : '',
      assignDays: generatorForm.assignDays,
      
      exp_month_type_0: generatorForm.monthType === '0' ? 'on' : '',
      exp_month_type_1: generatorForm.monthType === '1' ? 'on' : '',
      assignMonths: generatorForm.assignMonths,
      
      use_week_ck: generatorForm.useWeek ? 'on' : '',
      exp_week_type_0: generatorForm.weekType === '0' ? 'on' : '',
      exp_week_type_1: generatorForm.weekType === '1' ? 'on' : '',
      assignWeeks: generatorForm.assignWeeks,
      
      use_year_ck: generatorForm.useYear ? 'on' : '',
      exp_year_type_0: generatorForm.yearType === '0' ? 'on' : '',
      exp_year_type_1: generatorForm.yearType === '1' ? 'on' : '',
      yearCronExp: generatorForm.yearExp
    }
    
    // 发送请求生成Cron表达式
    const response = await cronApi.generateCronExpression(formData)
    
    // 更新结果
    cronForm.expression = response.cronExpression
    
    // 更新解析结果
    cronParts.secLabel = response.secLabel
    cronParts.minLabel = response.minLabel
    cronParts.hhLabel = response.hhLabel
    cronParts.dayLabel = response.dayLabel
    cronParts.monthLabel = response.monthLabel
    cronParts.weekLabel = response.weekLabel
    cronParts.yearLabel = response.yearLabel
    cronParts.startDate = response.startDate
    
    // 更新下次执行时间
    nextFireTimes.value = response.schedulerNextResults.map(time => ({ time }))
    
    ElMessage.success('成功生成Cron表达式')
    activeTab.value = 'explanation'
  } catch (error) {
    console.error('生成Cron表达式失败:', error)
    ElMessage.error('生成Cron表达式失败')
  } finally {
    generating.value = false
  }
}
</script>

<style scoped>
.cron-container {
  padding: 10px;
}

.cron-card {
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

.cron-input-section {
  margin-bottom: 20px;
}

.cron-input-group {
  display: flex;
  gap: 10px;
}

.cron-tabs {
  margin-top: 20px;
}

.cron-parts {
  max-width: 600px;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cycle-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.range-separator {
  font-size: 16px;
  color: #606266;
}

.flex-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style> 