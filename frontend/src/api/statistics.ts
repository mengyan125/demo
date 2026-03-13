import request from '@/utils/request'

/**
 * 统计数据类型定义
 */
export interface StatisticsParams {
  startDate?: string
  endDate?: string
  pageNum?: number
  pageSize?: number
}

export interface TeacherStatistics {
  rank: number
  grade: string
  class: string
  subject: string
  name: string
  count: number
}

export interface CategoryStatistics {
  name: string
  value: number
}

export interface TrendStatistics {
  date: string
  count: number
}

export interface StatisticsOverview {
  todayCount: number
  monthCount: number
  monthCategoryCount: string
}

/**
 * 获取统计概览（今日、本月、类别最多）
 */
export function getStatisticsOverview() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

/**
 * 获取反馈类别占比分布
 */
export function getCategoryStatistics() {
  return request({
    url: '/statistics/category',
    method: 'get'
  })
}

/**
 * 获取反馈趋势统计
 */
export function getTrendStatistics(params: { type: 'week' | 'month' }) {
  return request({
    url: '/statistics/trend',
    method: 'get',
    params
  })
}

/**
 * 获取教师反馈统计（Top 10）
 */
export function getTeacherStatistics(params: StatisticsParams) {
  return request({
    url: '/statistics/teacher',
    method: 'get',
    params
  })
}

/**
 * 导出教师反馈统计
 */
export function exportTeacherStatistics(params: StatisticsParams) {
  return request({
    url: '/statistics/teacher/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 获取教师详情
 */
export function getTeacherDetail(teacherId: string | number) {
  return request({
    url: `/statistics/teacher/${teacherId}`,
    method: 'get'
  })
}
