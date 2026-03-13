import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/student',
    name: 'StudentLayout',
    component: () => import('@/layouts/StudentLayout.vue'),
    meta: { title: '学生端', requiresAuth: true, role: 'student' },
    children: [
      {
        path: 'feedback',
        name: 'StudentFeedback',
        component: () => import('@/views/student/Feedback.vue'),
        meta: { title: '我要反馈' }
      },
      {
        path: 'track',
        name: 'StudentTrack',
        component: () => import('@/views/student/Track.vue'),
        meta: { title: '反馈跟踪' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { title: '管理端', requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '首页统计' }
      },
      {
        path: 'feedback',
        name: 'AdminFeedback',
        component: () => import('@/views/admin/Feedback.vue'),
        meta: { title: '反馈查看' }
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '反馈统计' }
      },
      {
        path: 'teacher/:teacherId',
        name: 'TeacherDetail',
        component: () => import('@/views/admin/TeacherDetail.vue'),
        meta: { title: '教师反馈详情' }
      },
      {
        path: 'category',
        name: 'AdminCategory',
        component: () => import('@/views/admin/Category.vue'),
        meta: { title: '类别管理' }
      },
      {
        path: 'permission',
        name: 'AdminPermission',
        component: () => import('@/views/admin/Permission.vue'),
        meta: { title: '权限配置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.meta.requiresAuth !== false

  // 设置页面标题
  document.title = `${to.meta.title || '学生反馈系统'} - 学生反馈系统`

  // 如果不需要认证，直接放行
  if (!requiresAuth) {
    next()
    return
  }

  // 检查是否已登录
  if (!userStore.isLoggedIn) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 检查角色权限
  const requiredRole = to.meta.role as string
  if (requiredRole && userStore.userRole !== requiredRole) {
    // 如果角色不匹配，重定向到对应角色的首页
    if (userStore.isStudent) {
      next('/student/feedback')
    } else {
      next('/admin/dashboard')
    }
    return
  }

  next()
})

export default router
