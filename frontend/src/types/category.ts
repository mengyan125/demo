/**
 * 反馈类别相关类型定义
 */

// 反馈类别
export interface Category {
  id: number
  name: string
  description?: string
  requireTeacher: boolean // 是否必须选择教师
  icon?: string
  sort: number // 排序
  createTime: string
}

// 创建类别请求
export interface CreateCategoryRequest {
  name: string
  description?: string
  requireTeacher: boolean
  icon?: string
  sort?: number
}

// 更新类别请求
export interface UpdateCategoryRequest {
  id: number
  name?: string
  description?: string
  requireTeacher?: boolean
  icon?: string
  sort?: number
}
