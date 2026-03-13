/**
 * 反馈相关类型定义
 */

// 反馈状态枚举
export enum FeedbackStatus {
  PENDING = 'pending', // 待处理
  PROCESSING = 'processing', // 处理中
  REPLIED = 'replied', // 已回复
  CLOSED = 'closed' // 已关闭
}

// 反馈信息
export interface Feedback {
  id: number
  categoryId: number
  categoryName: string
  teacherId?: number
  teacherName?: string
  content: string
  status: FeedbackStatus
  studentId: number
  studentName: string
  className: string
  attachments: Attachment[]
  replies: Reply[]
  remarks: Remark[]
  createTime: string
  updateTime: string
}

// 附件信息
export interface Attachment {
  id: number
  name: string
  url: string
  size: number
  type: string
}

// 回复信息
export interface Reply {
  id: number
  content: string
  adminId: number
  adminName: string
  createTime: string
}

// 备注信息（管理员之间协作）
export interface Remark {
  id: number
  content: string
  adminId: number
  adminName: string
  createTime: string
}

// 提交反馈请求
export interface SubmitFeedbackRequest {
  categoryId: number
  teacherId?: number
  content: string
  attachments: string[] // 附件URL数组
}

// 回复反馈请求
export interface ReplyFeedbackRequest {
  feedbackId: number
  content: string
}

// 添加备注请求
export interface AddRemarkRequest {
  feedbackId: number
  content: string
}

// 反馈筛选条件
export interface FeedbackFilter {
  status?: FeedbackStatus
  categoryId?: number
  teacherId?: number
  startTime?: string
  endTime?: string
  keyword?: string
}
