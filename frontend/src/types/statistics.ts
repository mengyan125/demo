/**
 * 统计相关类型定义
 */

// 首页统计数据
export interface HomeStatistics {
  todayCount: number // 今日反馈数
  monthCount: number // 本月反馈数
  totalCount: number // 总反馈数
  pendingCount: number // 待处理数
  topCategory: {
    name: string
    count: number
  }
}

// 类别统计数据
export interface CategoryStatistics {
  categoryId: number
  categoryName: string
  count: number
  percentage: number
}

// 趋势统计数据
export interface TrendStatistics {
  date: string
  count: number
}

// 教师统计数据
export interface TeacherStatistics {
  teacherId: number
  teacherName: string
  count: number
  rank: number
}
