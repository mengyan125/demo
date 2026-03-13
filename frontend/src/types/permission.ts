/**
 * 权限相关类型定义
 */

// 权限信息
export interface Permission {
  id: number
  adminId: number
  adminName: string
  categoryIds: number[] // 可查看的类别ID列表
  createTime: string
}

// 分配权限请求
export interface AssignPermissionRequest {
  adminId: number
  categoryIds: number[]
}
