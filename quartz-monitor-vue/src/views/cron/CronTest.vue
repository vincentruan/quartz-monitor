<template>
  <div>
    <h2>Cron 测试页面</h2>
    <p>这是一个测试组件，用于验证 mock 服务是否正常工作。</p>
    
    <div class="test-area">
      <h3>API 测试</h3>
      <el-button @click="testApi" :loading="testing">测试 API</el-button>
      <div v-if="apiResponse" class="response-area">
        <pre>{{ JSON.stringify(apiResponse, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { cronApi } from '@/api'

const apiResponse = ref(null)
const testing = ref(false)

const testApi = async () => {
  testing.value = true
  try {
    apiResponse.value = await cronApi.validateCronExpression('0 0 12 * * ?')
    console.log('API响应:', apiResponse.value)
  } catch (error) {
    console.error('API测试失败:', error)
    apiResponse.value = { error: error.message }
  } finally {
    testing.value = false
  }
}
</script>

<style scoped>
.test-area {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.response-area {
  margin-top: 20px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
  overflow: auto;
  max-height: 300px;
}
</style> 