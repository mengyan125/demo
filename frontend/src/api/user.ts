import request from '@/utils/request'
import type { LoginRequest, LoginResponse, User, Teacher, Class } from '@/types'

/**
 * 用户登录
 */
export function login(data: LoginRequest) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取教师列表
 */
export function getTeacherList() {
  return request({
    url: '/user/teachers',
    method: 'get'
  })
}

/**
 * 获取班级列表
 */
export function getClassList() {
  return request({
    url: '/user/classes',
    method: 'get'
  })
}
