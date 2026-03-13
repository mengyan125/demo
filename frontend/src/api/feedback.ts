import request from '@/utils/request'
import type {
  Feedback,
  SubmitFeedbackRequest,
  ReplyFeedbackRequest,
  AddRemarkRequest,
  FeedbackFilter,
  PageParams,
  PageData
} from '@/types'

/**
 * 提交反馈
 */
export function submitFeedback(data: SubmitFeedbackRequest) {
  return request({
    url: '/feedback/submit',
    method: 'post',
    data
  })
}

/**
 * 获取我的反馈列表（学生端）
 */
export function getMyFeedbackList(params: PageParams) {
  return request({
    url: '/feedback/my',
    method: 'get',
    params
  })
}

/**
 * 获取反馈列表（管理员端）
 */
export function getFeedbackList(params: PageParams & FeedbackFilter) {
  return request({
    url: '/feedback/list',
    method: 'get',
    params
  })
}

/**
 * 获取反馈详情
 */
export function getFeedbackDetail(id: number) {
  return request({
    url: `/feedback/${id}`,
    method: 'get'
  })
}

/**
 * 回复反馈
 */
export function replyFeedback(data: ReplyFeedbackRequest) {
  return request({
    url: '/feedback/reply',
    method: 'post',
    data
  })
}

/**
 * 添加备注
 */
export function addRemark(data: AddRemarkRequest) {
  return request({
    url: '/feedback/remark',
    method: 'post',
    data
  })
}

/**
 * 关闭反馈
 */
export function closeFeedback(id: number) {
  return request({
    url: `/feedback/${id}/close`,
    method: 'put'
  })
}

/**
 * 补充反馈信息
 */
export function supplementFeedback(id: number, content: string) {
  return request({
    url: `/feedback/${id}/supplement`,
    method: 'post',
    data: { content }
  })
}

/**
 * 获取教师的反馈列表
 */
export function getTeacherFeedbackList(teacherId: string | number, params: PageParams) {
  return request({
    url: `/feedback/teacher/${teacherId}`,
    method: 'get',
    params
  })
}
