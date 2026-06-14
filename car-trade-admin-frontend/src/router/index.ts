import { createRouter, createWebHistory } from 'vue-router'
import { hasAuthToken } from '../composables/useAuth'
import AdminLayout from '../layouts/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/index.vue'),
      meta: { public: true, title: '登录' },
    },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '分析仪表盘' },
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('../views/users/index.vue'),
          meta: { title: '用户管理' },
        },
        {
          path: 'dealers',
          name: 'dealers',
          component: () => import('../views/dealers/index.vue'),
          meta: { title: '车行管理' },
        },
        {
          path: 'dealer-audit',
          name: 'dealer-audit',
          component: () => import('../views/dealer-audit/index.vue'),
          meta: { title: '车行注册审核' },
        },
        {
          path: 'vehicles',
          name: 'vehicles',
          component: () => import('../views/vehicles/index.vue'),
          meta: { title: '5D 车源管理' },
        },
        {
          path: 'purchase',
          name: 'purchase',
          component: () => import('../views/purchase/index.vue'),
          meta: { title: '求购管理' },
        },
        {
          path: 'transactions',
          name: 'transactions',
          component: () => import('../views/transactions/index.vue'),
          meta: { title: '交易管理' },
        },
        {
          path: 'deposit',
          name: 'deposit',
          component: () => import('../views/deposit/index.vue'),
          meta: { title: '保证金现金流' },
        },
        {
          path: 'export-config',
          name: 'export-config',
          component: () => import('../views/export-config/index.vue'),
          meta: { title: '出口配置' },
        },
        {
          path: 'models',
          name: 'models',
          component: () => import('../views/models/index.vue'),
          meta: { title: '车型库' },
        },
        {
          path: 'resources',
          name: 'resources',
          component: () => import('../views/resources/index.vue'),
          meta: { title: '资源管理' },
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const isPublic = to.meta.public === true

  if (isPublic) {
    if (to.name === 'login' && hasAuthToken()) {
      return { path: '/dashboard' }
    }
    return true
  }

  if (!hasAuthToken()) {
    return {
      path: '/login',
      query: to.fullPath !== '/' ? { redirect: to.fullPath } : undefined,
    }
  }

  return true
})

export default router
