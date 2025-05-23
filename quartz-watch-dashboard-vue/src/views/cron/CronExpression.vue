<template>
  <div class="cron-expression container">
    <h2>Cron表达式验证和生成</h2>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="验证表达式" name="validate">
        <el-form :model="validateForm" label-width="100px">
          <el-form-item label="Cron表达式">
            <el-input v-model="validateForm.cronExpression" placeholder="输入Cron表达式">
              <el-button slot="append" @click="validateCron">验证</el-button>
            </el-input>
          </el-form-item>
        </el-form>
        
        <div v-if="validateResult.length" class="result-section">
          <h3>未来10次执行时间:</h3>
          <el-table :data="validateResult" border style="width: 100%">
            <el-table-column prop="index" label="#" width="50"></el-table-column>
            <el-table-column prop="time" label="执行时间"></el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="生成表达式" name="generate">
        <el-form :model="generateForm" label-width="100px">
          <el-form-item label="秒">
            <el-select v-model="generateForm.second" placeholder="选择秒">
              <el-option label="每秒 (*)" value="*"></el-option>
              <el-option label="每隔 n 秒 (/)" value="/"></el-option>
              <el-option label="指定秒数 (,)" value=","></el-option>
              <el-option label="区间 (-)" value="-"></el-option>
            </el-select>
            <div v-if="generateForm.second === '/'" class="sub-option">
              <span>从</span>
              <el-input-number v-model="generateForm.secondStart" :min="0" :max="59" size="small"></el-input-number>
              <span>秒开始，每</span>
              <el-input-number v-model="generateForm.secondInterval" :min="1" :max="59" size="small"></el-input-number>
              <span>秒</span>
            </div>
            <div v-if="generateForm.second === ','">
              <el-select v-model="generateForm.secondList" multiple placeholder="选择秒数">
                <el-option v-for="i in 60" :key="`second-${i-1}`" :label="i-1" :value="i-1"></el-option>
              </el-select>
            </div>
            <div v-if="generateForm.second === '-'" class="sub-option">
              <span>从</span>
              <el-input-number v-model="generateForm.secondRangeStart" :min="0" :max="59" size="small"></el-input-number>
              <span>到</span>
              <el-input-number v-model="generateForm.secondRangeEnd" :min="0" :max="59" size="small"></el-input-number>
            </div>
          </el-form-item>
          
          <el-form-item label="分钟">
            <el-select v-model="generateForm.minute" placeholder="选择分钟">
              <el-option label="每分钟 (*)" value="*"></el-option>
              <el-option label="每隔 n 分钟 (/)" value="/"></el-option>
              <el-option label="指定分钟 (,)" value=","></el-option>
              <el-option label="区间 (-)" value="-"></el-option>
            </el-select>
            <div v-if="generateForm.minute === '/'" class="sub-option">
              <span>从</span>
              <el-input-number v-model="generateForm.minuteStart" :min="0" :max="59" size="small"></el-input-number>
              <span>分开始，每</span>
              <el-input-number v-model="generateForm.minuteInterval" :min="1" :max="59" size="small"></el-input-number>
              <span>分钟</span>
            </div>
            <div v-if="generateForm.minute === ','">
              <el-select v-model="generateForm.minuteList" multiple placeholder="选择分钟">
                <el-option v-for="i in 60" :key="`minute-${i-1}`" :label="i-1" :value="i-1"></el-option>
              </el-select>
            </div>
            <div v-if="generateForm.minute === '-'" class="sub-option">
              <span>从</span>
              <el-input-number v-model="generateForm.minuteRangeStart" :min="0" :max="59" size="small"></el-input-number>
              <span>到</span>
              <el-input-number v-model="generateForm.minuteRangeEnd" :min="0" :max="59" size="small"></el-input-number>
            </div>
          </el-form-item>
          
          <el-form-item label="小时">
            <el-select v-model="generateForm.hour" placeholder="选择小时">
              <el-option label="每小时 (*)" value="*"></el-option>
              <el-option label="每隔 n 小时 (/)" value="/"></el-option>
              <el-option label="指定小时 (,)" value=","></el-option>
              <el-option label="区间 (-)" value="-"></el-option>
            </el-select>
            <div v-if="generateForm.hour === '/'" class="sub-option">
              <span>从</span>
              <el-input-number v-model="generateForm.hourStart" :min="0" :max="23" size="small"></el-input-number>
              <span>时开始，每</span>
              <el-input-number v-model="generateForm.hourInterval" :min="1" :max="23" size="small"></el-input-number>
              <span>小时</span>
            </div>
            <div v-if="generateForm.hour === ','">
              <el-select v-model="generateForm.hourList" multiple placeholder="选择小时">
                <el-option v-for="i in 24" :key="`hour-${i-1}`" :label="i-1" :value="i-1"></el-option>
              </el-select>
            </div>
            <div v-if="generateForm.hour === '-'" class="sub-option">
              <span>从</span>
              <el-input-number v-model="generateForm.hourRangeStart" :min="0" :max="23" size="small"></el-input-number>
              <span>到</span>
              <el-input-number v-model="generateForm.hourRangeEnd" :min="0" :max="23" size="small"></el-input-number>
            </div>
          </el-form-item>
          
          <el-form-item label="日期">
            <el-select v-model="generateForm.dayOfMonth" placeholder="选择日期">
              <el-option label="每日 (*)" value="*"></el-option>
              <el-option label="指定日期 (,)" value=","></el-option>
              <el-option label="不指定 (?)" value="?"></el-option>
              <el-option label="每月最后一天 (L)" value="L"></el-option>
            </el-select>
            <div v-if="generateForm.dayOfMonth === ','">
              <el-select v-model="generateForm.dayOfMonthList" multiple placeholder="选择日期">
                <el-option v-for="i in 31" :key="`day-${i}`" :label="i" :value="i"></el-option>
              </el-select>
            </div>
          </el-form-item>
          
          <el-form-item label="月份">
            <el-select v-model="generateForm.month" placeholder="选择月份">
              <el-option label="每月 (*)" value="*"></el-option>
              <el-option label="指定月份 (,)" value=","></el-option>
            </el-select>
            <div v-if="generateForm.month === ','">
              <el-select v-model="generateForm.monthList" multiple placeholder="选择月份">
                <el-option v-for="(month, index) in months" :key="`month-${index+1}`" :label="month" :value="index+1"></el-option>
              </el-select>
            </div>
          </el-form-item>
          
          <el-form-item label="星期">
            <el-select v-model="generateForm.dayOfWeek" placeholder="选择星期">
              <el-option label="每天 (*)" value="*"></el-option>
              <el-option label="指定星期 (,)" value=","></el-option>
              <el-option label="不指定 (?)" value="?"></el-option>
            </el-select>
            <div v-if="generateForm.dayOfWeek === ','">
              <el-select v-model="generateForm.dayOfWeekList" multiple placeholder="选择星期">
                <el-option v-for="(day, index) in daysOfWeek" :key="`week-${index+1}`" :label="day" :value="index+1"></el-option>
              </el-select>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="generateCron">生成表达式</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
        
        <div v-if="generatedCron" class="result-section">
          <h3>生成的Cron表达式:</h3>
          <el-input v-model="generatedCron" readonly>
            <el-button slot="append" @click="copyToClipboard">复制</el-button>
          </el-input>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'CronExpression',
  data() {
    return {
      activeTab: 'validate',
      validateForm: {
        cronExpression: ''
      },
      validateResult: [],
      generateForm: {
        second: '*',
        minute: '*',
        hour: '*',
        dayOfMonth: '*',
        month: '*',
        dayOfWeek: '?',
        secondStart: 0,
        secondInterval: 1,
        secondList: [],
        secondRangeStart: 0,
        secondRangeEnd: 59,
        minuteStart: 0,
        minuteInterval: 1,
        minuteList: [],
        minuteRangeStart: 0,
        minuteRangeEnd: 59,
        hourStart: 0,
        hourInterval: 1,
        hourList: [],
        hourRangeStart: 0,
        hourRangeEnd: 23,
        dayOfMonthList: [],
        monthList: [],
        dayOfWeekList: []
      },
      generatedCron: '',
      months: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
      daysOfWeek: ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    }
  },
  methods: {
    validateCron() {
      if (!this.validateForm.cronExpression) {
        this.$message.error('请输入Cron表达式')
        return
      }
      
      // 这里简单模拟 - 实际情况下应该调用后端API来验证
      this.validateResult = []
      const now = new Date()
      let nextDate = new Date(now)
      
      // 模拟生成10个执行时间
      for (let i = 1; i <= 10; i++) {
        nextDate = this.getNextExecTime(nextDate)
        this.validateResult.push({
          index: i,
          time: this.formatDate(nextDate)
        })
      }
    },
    
    // 简单模拟下一次执行时间计算
    getNextExecTime(date) {
      // 简单的模拟，加上一些随机分钟
      const next = new Date(date)
      next.setMinutes(next.getMinutes() + Math.floor(Math.random() * 60 + 1))
      return next
    },
    
    formatDate(date) {
      return date.toLocaleString()
    },
    
    generateCron() {
      const parts = []
      
      // 秒
      if (this.generateForm.second === '*') {
        parts.push('*')
      } else if (this.generateForm.second === '/') {
        parts.push(`${this.generateForm.secondStart}/${this.generateForm.secondInterval}`)
      } else if (this.generateForm.second === ',') {
        parts.push(this.generateForm.secondList.join(','))
      } else if (this.generateForm.second === '-') {
        parts.push(`${this.generateForm.secondRangeStart}-${this.generateForm.secondRangeEnd}`)
      }
      
      // 分钟
      if (this.generateForm.minute === '*') {
        parts.push('*')
      } else if (this.generateForm.minute === '/') {
        parts.push(`${this.generateForm.minuteStart}/${this.generateForm.minuteInterval}`)
      } else if (this.generateForm.minute === ',') {
        parts.push(this.generateForm.minuteList.join(','))
      } else if (this.generateForm.minute === '-') {
        parts.push(`${this.generateForm.minuteRangeStart}-${this.generateForm.minuteRangeEnd}`)
      }
      
      // 小时
      if (this.generateForm.hour === '*') {
        parts.push('*')
      } else if (this.generateForm.hour === '/') {
        parts.push(`${this.generateForm.hourStart}/${this.generateForm.hourInterval}`)
      } else if (this.generateForm.hour === ',') {
        parts.push(this.generateForm.hourList.join(','))
      } else if (this.generateForm.hour === '-') {
        parts.push(`${this.generateForm.hourRangeStart}-${this.generateForm.hourRangeEnd}`)
      }
      
      // 日期
      if (this.generateForm.dayOfMonth === '*' || this.generateForm.dayOfMonth === '?' || this.generateForm.dayOfMonth === 'L') {
        parts.push(this.generateForm.dayOfMonth)
      } else if (this.generateForm.dayOfMonth === ',') {
        parts.push(this.generateForm.dayOfMonthList.join(','))
      }
      
      // 月份
      if (this.generateForm.month === '*') {
        parts.push('*')
      } else if (this.generateForm.month === ',') {
        parts.push(this.generateForm.monthList.join(','))
      }
      
      // 星期
      if (this.generateForm.dayOfWeek === '*' || this.generateForm.dayOfWeek === '?') {
        parts.push(this.generateForm.dayOfWeek)
      } else if (this.generateForm.dayOfWeek === ',') {
        parts.push(this.generateForm.dayOfWeekList.join(','))
      }
      
      this.generatedCron = parts.join(' ')
    },
    
    copyToClipboard() {
      const el = document.createElement('textarea')
      el.value = this.generatedCron
      document.body.appendChild(el)
      el.select()
      document.execCommand('copy')
      document.body.removeChild(el)
      this.$message.success('已复制到剪贴板')
    },
    
    resetForm() {
      this.generateForm = {
        second: '*',
        minute: '*',
        hour: '*',
        dayOfMonth: '*',
        month: '*',
        dayOfWeek: '?',
        secondStart: 0,
        secondInterval: 1,
        secondList: [],
        secondRangeStart: 0,
        secondRangeEnd: 59,
        minuteStart: 0,
        minuteInterval: 1,
        minuteList: [],
        minuteRangeStart: 0,
        minuteRangeEnd: 59,
        hourStart: 0,
        hourInterval: 1,
        hourList: [],
        hourRangeStart: 0,
        hourRangeEnd: 23,
        dayOfMonthList: [],
        monthList: [],
        dayOfWeekList: []
      }
      this.generatedCron = ''
    }
  }
}
</script>

<style scoped>
.cron-expression {
  max-width: 900px;
  margin: 0 auto;
}

.sub-option {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.sub-option span {
  margin: 0 5px;
}

.result-section {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #f9fafc;
}
</style> 