<template>
  <div class="teacher-detail-page">
    <!-- 反馈列表 -->
    <div class="feedback-list-container">
      <div class="list-header">
        <span class="header-title">{{ teacherName }}被反馈次数：<strong>{{ totalCount }}</strong></span>
      </div>
      <div class="feedback-list">
        <div v-if="feedbackList.length === 0" class="empty-state">
          <el-empty description="暂无反馈数据" />
        </div>
        <div v-for="item in feedbackList" :key="item.id" class="feedback-item">
          <!-- 左侧：标题和元数据 -->
          <div class="item-left">
            <div class="item-title">{{ item.categoryName }} - {{ item.studentName }}</div>
            <div class="item-meta">
              <span class="meta-item">年级：{{ item.grade }}</span>
              <span class="meta-item">班级：{{ item.class }}</span>
              <span class="meta-item">反馈对象：{{ item.subject }}</span>
              <span class="meta-item">时间：{{ formatDate(item.createTime) }}</span>
            </div>
          </div>

          <!-- 右侧：收藏和标签 -->
          <div class="item-right">
            <el-icon class="collect-icon" @click="handleCollect(item)">
              <StarFilled />
            </el-icon>
            <span class="status-tag" :class="`status-${item.status}`">
              {{ getStatusText(item.status) }}
            </span>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalCount"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { StarFilled } from '@element-plus/icons-vue'
import { getTeacherDetail } from '@/api/statistics'
import { getTeacherFeedbackList } from '@/api/feedback'
import type { Feedback } from '@/types/feedback'

const router = useRouter()
const route = useRoute()

// 教师信息
const teacherName = ref('')

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)

// 反馈列表
const feedbackList = ref<Feedback[]>([])

// 获取教师信息
async function getTeacherInfo() {
  try {
    const { teacherId } = route.params
    const response = await getTeacherDetail(teacherId)
    teacherName.value = response.data?.name || '未知教师'
  } catch (error) {
    console.error('获取教师信息失败：', error)
    ElMessage.error('获取教师信息失败')
  }
}


// 获取反馈列表
async function fetchFeedbackList() {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const response = await getTeacherFeedbackList(route.params.teacherId, params)
    feedbackList.value = response.data?.list || []
    totalCount.value = response.data?.total || 0
  } catch (error) {
    console.error('获取反馈列表失败：', error)
    ElMessage.error('获取反馈列表失败')
  }
}

// 分页变化
function handlePageChange() {
  fetchFeedbackList()
}

// 收藏反馈
function handleCollect(item: Feedback) {
  ElMessage.success('已收藏')
}

// 格式化日期
function formatDate(date: string) {
  return date || ''
}

// 获取状态文本
function getStatusText(status: string) {
  const statusMap: Record<string, string> = {
    pending: '待处理',
    replied: '已回复',
    closed: '已关闭'
  }
  return statusMap[status] || status
}

// 初始化
onMounted(() => {
  getTeacherInfo()
  fetchFeedbackList()
})
</script>

<style scoped>
.teacher-detail-page {
  width: 100%;
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 反馈列表容器 */
.feedback-list-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.list-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
  background: #fff;
}

.header-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.list-header strong {
  color: #ff3333;
  font-weight: 600;
}

/* 反馈列表 */
.feedback-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 20px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.feedback-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 0;
  border-left: 4px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
}

.feedback-item:hover {
  border-left-color: #ff3333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.item-left {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  margin-bottom: 6px;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #999;
  flex-wrap: wrap;
}

.meta-item {
  white-space: nowrap;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  margin-left: 12px;
}

.collect-icon {
  font-size: 16px;
  color: #ccc;
  cursor: pointer;
  transition: color 0.3s;
}

.collect-icon:hover {
  color: #ff3333;
}

.status-tag {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 11px;
  font-weight: 500;
  white-space: nowrap;
}

.status-pending {
  background: #fef0f0;
  color: #f56c6c;
}

.status-replied {
  background: #f0f9ff;
  color: #0066cc;
}

.status-closed {
  background: #f0f0f0;
  color: #909399;
}

/* 分页 */
.pagination {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #e8e8e8;
  flex-shrink: 0;
}
</style>
