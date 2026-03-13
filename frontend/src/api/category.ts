import request from '@/utils/request'
import type { Category, CreateCategoryRequest, UpdateCategoryRequest } from '@/types'

/**
 * 获取反馈类别列表
 */
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

/**
 * 创建反馈类别
 */
export function createCategory(data: CreateCategoryRequest) {
  return request({
    url: '/category/create',
    method: 'post',
    data
  })
}

/**
 * 更新反馈类别
 */
export function updateCategory(data: UpdateCategoryRequest) {
  return request({
    url: '/category/update',
    method: 'put',
    data
  })
}

/**
 * 删除反馈类别
 */
export function deleteCategory(id: number) {
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}
