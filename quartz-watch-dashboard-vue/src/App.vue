<template>
  <div class="app">
    <el-config-provider>
      <router-view />
    </el-config-provider>
  </div>
</template>

<script setup>
// 根组件
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

#app {
  height: 100%;
}

.app {
  height: 100%;
}
</style> 