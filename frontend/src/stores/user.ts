import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { User, LoginRequest, UserRole } from '@/types'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role)
  const isStudent = computed(() => userRole.value === 'student')
  const isAdmin = computed(() => {
    return (
      userRole.value === 'category_admin' ||
      userRole.value === 'dept_leader' ||
      userRole.value === 'school_leader' ||
      userRole.value === 'system_admin'
    )
  })

  // 登录
  async function login(loginData: LoginRequest) {
    try {
      // TODO: 临时 Mock 登录，后续替换为真实 API
      // 模拟网络延迟
      await new Promise((resolve) => setTimeout(resolve, 500))

      // Mock 用户数据
      let mockUser: User
      let mockToken: string

      // 学生账号：student / 123456
      if (loginData.username === 'student' && loginData.password === '123456') {
        mockToken = 'mock-student-token-' + Date.now()
        mockUser = {
          id: 1,
          username: 'student',
          realName: '张三',
          role: 'student',
          classId: 1,
          className: '高一(1)班',
          createTime: '2024-01-01 00:00:00'
        }
      }
      // 管理员账号：admin / 123456
      else if (loginData.username === 'admin' && loginData.password === '123456') {
        mockToken = 'mock-admin-token-' + Date.now()
        mockUser = {
          id: 2,
          username: 'admin',
          realName: '李老师',
          role: 'category_admin',
          createTime: '2024-01-01 00:00:00'
        }
      }
      // 错误的账号密码
      else {
        ElMessage.error('用户名或密码错误')
        return false
      }

      // 保存 token 和用户信息
      token.value = mockToken
      userInfo.value = mockUser
      localStorage.setItem('token', mockToken)
      localStorage.setItem('user', JSON.stringify(mockUser))

      ElMessage.success('登录成功')
      return true

      // 真实 API 调用（暂时注释）
      // const res = await loginApi(loginData)
      // const { token: newToken, user } = res.data
      // token.value = newToken
      // userInfo.value = user
      // localStorage.setItem('token', newToken)
      // localStorage.setItem('user', JSON.stringify(user))
      // ElMessage.success('登录成功')
      // return true
    } catch (error) {
      console.error('登录失败：', error)
      return false
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
    } catch (error) {
      console.error('获取用户信息失败：', error)
      // 如果获取失败，清除本地数据
      logout()
    }
  }

  // 退出登录
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('退出登录失败：', error)
    } finally {
      // 清除本地数据
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('已退出登录')
    }
  }

  // 初始化用户信息（从 localStorage 恢复）
  function initUserInfo() {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      try {
        userInfo.value = JSON.parse(savedUser)
      } catch (error) {
        console.error('解析用户信息失败：', error)
        localStorage.removeItem('user')
      }
    }
  }

  // 初始化
  initUserInfo()

  return {
    token,
    userInfo,
    isLoggedIn,
    userRole,
    isStudent,
    isAdmin,
    login,
    logout,
    fetchUserInfo
  }
})
