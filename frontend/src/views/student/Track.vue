<template>
  <div class="track-page">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">意见反馈</span>
        </div>
        <div class="nav-links">
          <router-link to="/student/feedback" class="nav-link">我要反馈</router-link>
          <router-link to="/student/track" class="nav-link active">反馈跟踪</router-link>
        </div>
        <div class="nav-right">
          <el-icon><Setting /></el-icon>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="content-wrapper">
      <!-- 筛选条件 -->
      <div class="filter-bar">
        <div class="filter-item">
          <label>类别：</label>
          <el-select
            v-model="filterCategory"
            placeholder="全部类别"
            clearable
            @change="handleFilter"
          >
            <el-option label="全部类别" value="" />
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </div>

        <div class="filter-item search-item">
          <el-input
            v-model="searchKeyword"
            placeholder="关键词搜索"
            clearable
            @input="handleFilter"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 反馈列表 -->
      <div v-loading="loading" class="feedback-list">
        <el-empty v-if="feedbackList.length === 0" description="暂无反馈记录" />

        <div
          v-for="feedback in feedbackList"
          :key="feedback.id"
          class="feedback-card"
          @click="handleViewDetail(feedback.id)"
        >
          <!-- 状态标签 -->
          <div v-if="feedback.status === 'replied'" class="status-badge">已回复</div>

          <div class="card-content">
            <div class="card-header">
              <h3 class="feedback-title">{{ feedback.title }}</h3>
            </div>

            <div class="card-body">
              <div class="meta-row">
                <span class="meta-item">
                  <span class="label">反馈类别：</span>
                  <span class="value">{{ feedback.categoryName }}</span>
                </span>
                <span v-if="feedback.teacherName" class="meta-item">
                  <span class="label">反馈对象：</span>
                  <span class="value">{{ feedback.teacherName }}</span>
                </span>
                <span class="meta-item">
                  <span class="label">时间：</span>
                  <span class="value">{{ formatTime(feedback.createTime) }}</span>
                </span>
              </div>
              <p class="feedback-text">{{ truncateText(feedback.content, 100) }}</p>
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
        layout="total, prev, pager, next"
        class="pagination"
        @current-change="fetchFeedbackList"
      />
    </div>

    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="反馈详情"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="currentFeedback" v-loading="detailLoading" class="feedback-detail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
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
              {{ formatTime(currentFeedback.createTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 反馈内容 -->
        <div class="detail-section">
          <h4>反馈内容</h4>
          <div class="content-text">{{ currentFeedback.content }}</div>
        </div>

        <!-- 附件 -->
        <div v-if="currentFeedback.attachments && currentFeedback.attachments.length > 0" class="detail-section">
          <h4>附件</h4>
          <div class="attachment-list">
            <div
              v-for="attachment in currentFeedback.attachments"
              :key="attachment.id"
              class="attachment-item"
            >
              <el-link :href="attachment.url" target="_blank">
                {{ attachment.name }}
              </el-link>
            </div>
          </div>
        </div>

        <!-- 管理员回复 -->
        <div v-if="currentFeedback.replies && currentFeedback.replies.length > 0" class="detail-section">
          <h4>管理员回复</h4>
          <div class="reply-list">
            <div v-for="reply in currentFeedback.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="reply-admin">{{ reply.adminName }}</span>
                <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, Search } from '@element-plus/icons-vue'
import { getMyFeedbackList, getFeedbackDetail } from '@/api/feedback'
import { useFeedbackStore } from '@/stores/feedback'
import type { Feedback, FeedbackStatus } from '@/types'

const feedbackStore = useFeedbackStore()

// 筛选条件
const filterCategory = ref<number | ''>('')
const searchKeyword = ref('')

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

// 类别列表
const categories = computed(() => feedbackStore.categories)

// 获取反馈列表
async function fetchFeedbackList() {
  loading.value = true
  try {
    const res = await getMyFeedbackList({
      page: currentPage.value,
      pageSize: pageSize.value,
      categoryId: filterCategory.value || undefined,
      keyword: searchKeyword.value || undefined
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

// 查看详情
async function handleViewDetail(id: number) {
  detailVisible.value = true
  detailLoading.value = true

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

// 格式化时间
function formatTime(time: string) {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}

// 截断文本
function truncateText(text: string, length: number) {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '......' : text
}

// 初始化
onMounted(async () => {
  await feedbackStore.fetchCategories()
  await fetchFeedbackList()
})
</script>

<style scoped>
.track-page {
  min-height: 100%;
  background: #f5f5f5;
}

/* 顶部导航 */
.top-nav {
  background: #0099cc;
  color: white;
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 120px;
}

.logo-icon {
  font-size: 16px;
  white-space: nowrap;
}

.nav-links {
  display: flex;
  gap: 60px;
  flex: 1;
  justify-content: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 14px;
  padding: 8px 0;
  border-bottom: 3px solid transparent;
  transition: all 0.3s;
}

.nav-link:hover,
.nav-link.router-link-active {
  border-bottom-color: white;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 18px;
  cursor: pointer;
}

/* 内容区域 */
.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 筛选条件 */
.filter-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  background: white;
  padding: 15px;
  border-radius: 4px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-item label {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
}

.filter-item :deep(.el-select) {
  width: 150px;
}

.search-item {
  flex: 1;
  max-width: 300px;
}

.search-item :deep(.el-input) {
  width: 100%;
}

/* 反馈列表 */
.feedback-list {
  min-height: 400px;
}

.feedback-card {
  background: white;
  border-radius: 4px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.feedback-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #f56c6c;
  color: white;
  padding: 4px 12px;
  border-radius: 2px;
  font-size: 12px;
  font-weight: bold;
  z-index: 1;
}

.card-content {
  padding: 16px;
}

.card-header {
  margin-bottom: 12px;
}

.feedback-title {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  word-break: break-word;
  line-height: 1.5;
}

.card-body {
  margin-bottom: 0;
}

.meta-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  font-size: 13px;
  margin-bottom: 8px;
}

.meta-item {
  display: flex;
  gap: 6px;
}

.meta-item .label {
  color: #999;
}

.meta-item .value {
  color: #333;
}

.feedback-text {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 详情对话框 */
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
}

.content-text {
  padding: 12px;
  background: #f5f5f5;
  border-radius: 4px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 4px;
}

.reply-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  padding: 12px;
  background: #f5f5f5;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.reply-admin {
  font-weight: 600;
  color: #333;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  line-height: 1.6;
  color: #666;
}
</style>
