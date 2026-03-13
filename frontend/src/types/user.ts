/**
 * 用户相关类型定义
 */

// 用户角色枚举
export enum UserRole {
  STUDENT = 'student', // 学生
  CATEGORY_ADMIN = 'category_admin', // 类别管理员
  DEPT_LEADER = 'dept_leader', // 部门领导
  SCHOOL_LEADER = 'school_leader', // 校领导
  SYSTEM_ADMIN = 'system_admin' // 系统管理员
}

// 用户信息
export interface User {
  id: number
  username: string
  realName: string
  role: UserRole
  classId?: number // 学生的班级ID
  className?: string // 班级名称
  avatar?: string
  email?: string
  phone?: string
  createTime: string
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应
export interface LoginResponse {
  token: string
  user: User
}

// 教师信息
export interface Teacher {
  id: number
  name: string
  subject?: string // 科目
}

// 班级信息
export interface Class {
  id: number
  name: string
  grade: string // 年级
  studentCount: number
}
