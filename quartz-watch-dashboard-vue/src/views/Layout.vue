<template>
  <div class="layout">
    <el-container>
      <el-header class="header">
        <div class="logo">Quartz管理界面</div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="theme-select">
              主题 <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="changeTheme('default')">蓝色</el-dropdown-item>
                <el-dropdown-item @click="changeTheme('green')">绿色</el-dropdown-item>
                <el-dropdown-item @click="changeTheme('purple')">紫色</el-dropdown-item>
                <el-dropdown-item @click="changeTheme('silver')">银色</el-dropdown-item>
                <el-dropdown-item @click="changeTheme('azure')">天蓝</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <span class="contact">
            <a href="http://weibo.com/xishuixixia" target="_blank">联系我们</a>
          </span>
        </div>
      </el-header>

      <el-container>
        <el-aside width="200px" class="sidebar">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            router
          >
            <el-sub-menu index="1">
              <template #title>
                <el-icon><folder /></el-icon>
                <span>界面组件</span>
              </template>
              <el-menu-item index="/dashboard">我的主页</el-menu-item>
              <el-menu-item index="/jobs">任务列表</el-menu-item>
              <el-menu-item index="/cron">CronExpression验证</el-menu-item>
              <el-menu-item index="/test/cron">Cron 测试</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>

        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>

      <el-footer class="footer">
        Copyright &copy; {{ currentYear }} <a href="#" target="_blank">恒信CRM</a> Tel：010-52897073
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowDown, Folder } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
const currentYear = new Date().getFullYear()

const changeTheme = (theme) => {
  console.log('切换主题到:', theme)
  // 这里可以实现主题切换逻辑
}
</script>

<style scoped>
.layout {
  height: 100%;
}

.el-container {
  height: 100%;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 2px;
}

.header-right {
  display: flex;
  align-items: center;
}

.theme-select {
  cursor: pointer;
  color: white;
  margin-right: 20px;
  display: flex;
  align-items: center;
}

.contact a {
  color: white;
  text-decoration: none;
}

.sidebar {
  border-right: 1px solid #eee;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}

.footer {
  background-color: #f5f7fa;
  color: #606266;
  text-align: center;
  line-height: 60px;
  border-top: 1px solid #eee;
}
</style> 