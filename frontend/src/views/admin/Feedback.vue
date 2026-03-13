<template>
  <div class="feedback-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h3>反馈查看</h3>
          <p>查看和处理学生提交的反馈</p>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-bar">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 150px">
              <el-option label="待处理" value="pending" />
              <el-option label="处理中" value="processing" />
              <el-option label="已回复" value="replied" />
              <el-option label="已关闭" value="closed" />
            </el-select>
          </el-form-item>

          <el-form-item label="类别">
            <el-select v-model="filterForm.categoryId" placeholder="全部类别" clearable style="width: 150px">
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="关键词">
            <el-input
              v-model="filterForm.keyword"
              placeholder="搜索反馈内容"
              clearable
              style="width: 200px"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleFilter">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 反馈列表 -->
      <div v-loading="loading" class="feedback-list">
        <el-empty v-if="feedbackList.length === 0" description="暂无反馈记录" />

        <div
          v-for="feedback in feedbackList"
          :key="feedback.id"
          class="feedback-item"
          @click="handleViewDetail(feedback.id)"
        >
          <div class="feedback-header">
            <div class="header-left">
              <el-tag :type="getStatusType(feedback.status)">
                {{ getStatusText(feedback.status) }}
              </el-tag>
              <span class="feedback-category">{{ feedback.categoryName }}</span>
              <span v-if="feedback.teacherName" class="feedback-teacher">
                教师：{{ feedback.teacherName }}
              </span>
            </div>
            <span class="feedback-time">{{ feedback.createTime }}</span>
          </div>

          <div class="feedback-content">
            <div class="feedback-student">
              <el-icon><User /></el-icon>
              {{ feedback.studentName }} - {{ feedback.className }}
            </div>
            <div class="feedback-text">{{ feedback.content }}</div>
          </div>

          <div class="feedback-footer">
            <div class="footer-left">
              <span v-if="feedback.attachments.length > 0" class="has-attachment">
                <el-icon><Paperclip /></el-icon>
                {{ feedback.attachments.length }} 个附件
              </span>
              <span v-if="feedback.remarks.length > 0" class="has-remark">
                <el-icon><ChatDotRound /></el-icon>
                {{ feedback.remarks.length }} 条备注
              </span>
            </div>
            <div class="footer-right">
              <span v-if="feedback.replies.length > 0" class="reply-count">
                已回复 {{ feedback.replies.length }} 次
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50, 100]"
        class="pagination"
        @current-change="fetchFeedbackList"
        @size-change="fetchFeedbackList"
      />
    </el-card>

    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="反馈详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentFeedback" v-loading="detailLoading" class="feedback-detail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学生姓名">
              {{ currentFeedback.studentName }}
            </el-descriptions-item>
            <el-descriptions-item label="班级">
              {{ currentFeedback.className }}
            </el-descriptions-item>
            <el-descriptions-item label="反馈类别">
              {{ currentFeedback.categoryName }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentFeedback.status)">
                {{ getStatusText(currentFeedback.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentFeedback.teacherName" label="任课教师">
              {{ currentFeedback.teacherName }}
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">
              {{ currentFeedback.createTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 反馈内容 -->
        <div class="detail-section">
          <h4>反馈内容</h4>
          <div class="content-text">{{ currentFeedback.content }}</div>
        </div>

        <!-- 附件 -->
        <div v-if="currentFeedback.attachments.length > 0" class="detail-section">
          <h4>附件</h4>
          <div class="attachment-list">
            <div
              v-for="attachment in currentFeedback.attachments"
              :key="attachment.id"
              class="attachment-item"
            >
              <el-link :href="attachment.url" target="_blank">
                <el-icon><Document /></el-icon>
                {{ attachment.name }}
              </el-link>
            </div>
          </div>
        </div>

        <!-- 管理员回复 -->
        <div class="detail-section">
          <h4>管理员回复</h4>
          <div v-if="currentFeedback.replies.length > 0" class="reply-list">
            <div v-for="reply in currentFeedback.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="reply-admin">{{ reply.adminName }}</span>
                <span class="reply-time">{{ reply.createTime }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无回复" :image-size="60" />
        </div>

        <!-- 回复表单 -->
        <div class="detail-section">
          <h4>添加回复</h4>
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容..."
            maxlength="500"
            show-word-limit
          />
          <div class="reply-actions">
            <el-button type="primary" :loading="replying" @click="handleReply">
              提交回复
            </el-button>
          </div>
        </div>

        <!-- 备注 -->
        <div class="detail-section">
          <h4>内部备注</h4>
          <div v-if="currentFeedback.remarks.length > 0" class="remark-list">
            <div v-for="remark in currentFeedback.remarks" :key="remark.id" class="remark-item">
              <div class="remark-header">
                <span class="remark-admin">{{ remark.adminName }}</span>
                <span class="remark-time">{{ remark.createTime }}</span>
              </div>
              <div class="remark-content">{{ remark.content }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无备注" :image-size="60" />
        </div>

        <!-- 添加备注 -->
        <div class="detail-section">
          <h4>添加备注</h4>
          <el-input
            v-model="remarkContent"
            type="textarea"
            :rows="3"
            placeholder="添加内部备注（仅管理员可见）..."
            maxlength="200"
            show-word-limit
          />
          <div class="remark-actions">
            <el-button type="success" :loading="remarking" @click="handleAddRemark">
              添加备注
            </el-button>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentFeedback && currentFeedback.status !== 'closed'"
          type="danger"
          @click="handleClose"
        >
          关闭反馈
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useFeedbackStore } from '@/stores/feedback'
import {
  getFeedbackList,
  getFeedbackDetail,
  replyFeedback,
  addRemark,
  closeFeedback
} from '@/api/feedback'
import type { Feedback, FeedbackStatus, FeedbackFilter } from '@/types'

const feedbackStore = useFeedbackStore()

// 筛选表单
const filterForm = reactive<FeedbackFilter>({
  status: undefined,
  categoryId: undefined,
  keyword: ''
})

// 类别列表
const categories = ref(feedbackStore.categories)

// 反馈列表
const feedbackList = ref<Feedback[]>([])
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 详情对话框
const detailVisible = ref(false)
const detailLoading = ref(false)
const currentFeedback = ref<Feedback | null>(null)

// 回复
const replyContent = ref('')
const replying = ref(false)

// 备注
const remarkContent = ref('')
const remarking = ref(false)

// 获取反馈列表
async function fetchFeedbackList() {
  loading.value = true
  try {
    const res = await getFeedbackList({
      page: currentPage.value,
      pageSize: pageSize.value,
      ...filterForm
    })

    feedbackList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取反馈列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 筛选
function handleFilter() {
  currentPage.value = 1
  fetchFeedbackList()
}

// 重置
function handleReset() {
  filterForm.status = undefined
  filterForm.categoryId = undefined
  filterForm.keyword = ''
  currentPage.value = 1
  fetchFeedbackList()
}

// 查看详情
async function handleViewDetail(id: number) {
  detailVisible.value = true
  detailLoading.value = true
  replyContent.value = ''
  remarkContent.value = ''

  try {
    const res = await getFeedbackDetail(id)
    currentFeedback.value = res.data
  } catch (error) {
    console.error('获取反馈详情失败：', error)
    ElMessage.error('获取反馈详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 提交回复
async function handleReply() {
  if (!currentFeedback.value) return
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  replying.value = true
  try {
    await replyFeedback({
      feedbackId: currentFeedback.value.id,
      content: replyContent.value
    })

    ElMessage.success('回复成功')
    replyContent.value = ''

    // 刷新详情
    await handleViewDetail(currentFeedback.value.id)
    // 刷新列表
    await fetchFeedbackList()
  } catch (error) {
    console.error('回复失败：', error)
  } finally {
    replying.value = false
  }
}

// 添加备注
async function handleAddRemark() {
  if (!currentFeedback.value) return
  if (!remarkContent.value.trim()) {
    ElMessage.warning('请输入备注内容')
    return
  }

  remarking.value = true
  try {
    await addRemark({
      feedbackId: currentFeedback.value.id,
      content: remarkContent.value
    })

    ElMessage.success('备注添加成功')
    remarkContent.value = ''

    // 刷新详情
    await handleViewDetail(currentFeedback.value.id)
  } catch (error) {
    console.error('添加备注失败：', error)
  } finally {
    remarking.value = false
  }
}

// 关闭反馈
async function handleClose() {
  if (!currentFeedback.value) return

  try {
    await ElMessageBox.confirm('确定要关闭这条反馈吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await closeFeedback(currentFeedback.value.id)
    ElMessage.success('反馈已关闭')

    detailVisible.value = false
    await fetchFeedbackList()
  } catch (error) {
    // 用户取消或关闭失败
  }
}

// 获取状态类型
function getStatusType(status: FeedbackStatus) {
  const typeMap = {
    pending: 'info',
    processing: 'warning',
    replied: 'success',
    closed: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
function getStatusText(status: FeedbackStatus) {
  const textMap = {
    pending: '待处理',
    processing: '处理中',
    replied: '已回复',
    closed: '已关闭'
  }
  return textMap[status] || status
}

// 初始化
onMounted(async () => {
  await feedbackStore.fetchCategories()
  categories.value = feedbackStore.categories
  await fetchFeedbackList()
})
</script>

<style scoped>
.feedback-page {
  width: 100%;
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-card {
  box-shadow: none;
  background: #fff;
  border: none;
  border-radius: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.card-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.filter-bar {
  margin-bottom: 0;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 0;
  border: none;
  border-top: 1px solid #e8e8e8;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.filter-form {
  margin: 0;
}

.feedback-list {
  min-height: 0;
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.feedback-item {
  padding: 16px;
  margin-bottom: 12px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 4px solid transparent;
}

.feedback-item:hover {
  border-left-color: #ff3333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.feedback-category {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.feedback-teacher {
  font-size: 12px;
  color: #999;
}

.feedback-time {
  font-size: 12px;
  color: #999;
}

.feedback-content {
  margin-bottom: 12px;
}

.feedback-student {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.feedback-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.feedback-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.footer-left,
.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.has-attachment,
.has-remark {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.reply-count {
  font-size: 12px;
  color: #667eea;
  font-weight: 500;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
  padding: 0 20px 20px 20px;
  flex-shrink: 0;
}

.feedback-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  padding-left: 10px;
  border-left: 4px solid #ff3333;
}

.content-text {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 0;
  line-height: 1.6;
  white-space: pre-wrap;
  border: 1px solid #e8e8e8;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 0;
  border: 1px solid #e8e8e8;
}

.reply-list,
.remark-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.reply-item,
.remark-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 0;
  border: 1px solid #e8e8e8;
}

.reply-header,
.remark-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.reply-admin,
.remark-admin {
  font-weight: 600;
  color: #333;
}

.reply-time,
.remark-time {
  font-size: 12px;
  color: #999;
}

.reply-content,
.remark-content {
  line-height: 1.6;
  color: #666;
}

.reply-actions,
.remark-actions {
  margin-top: 12px;
  text-align: right;
}
</style>
