import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '我的主页' }
      },
      {
        path: 'jobs',
        name: 'Jobs',
        component: () => import('../views/Jobs.vue'),
        meta: { title: '任务列表' }
      },
      {
        path: 'cron',
        name: 'CronExpression',
        component: () => import('../views/CronExpressionNew.vue'),
        meta: { title: 'CronExpression验证' }
      },
      {
        path: 'test/cron',
        name: 'CronTest',
        component: () => import('../views/cron/CronTest.vue'),
        meta: { title: 'Cron 测试' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 