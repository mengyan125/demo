<template>
  <div class="dashboard-page">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon today">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.todayCount }}</div>
              <div class="stat-label">今日反馈</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon month">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.monthCount }}</div>
              <div class="stat-label">本月反馈</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingCount }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalCount }}</div>
              <div class="stat-label">总反馈数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最多反馈类别 -->
    <el-card class="chart-card" style="margin-top: 20px">
      <template #header>
        <h3>反馈最多的类别</h3>
      </template>
      <div v-if="statistics.topCategory" class="top-category">
        <div class="category-name">{{ statistics.topCategory.name }}</div>
        <div class="category-count">{{ statistics.topCategory.count }} 条反馈</div>
      </div>
      <el-empty v-else description="暂无数据" />
    </el-card>

    <!-- 快捷操作 -->
    <el-card class="action-card" style="margin-top: 20px">
      <template #header>
        <h3>快捷操作</h3>
      </template>
      <div class="action-buttons">
        <el-button type="primary" @click="goToFeedback">
          <el-icon><List /></el-icon>
          查看反馈
        </el-button>
        <el-button type="success" @click="goToStatistics">
          <el-icon><TrendCharts /></el-icon>
          查看统计
        </el-button>
        <el-button type="warning" @click="goToCategory">
          <el-icon><Folder /></el-icon>
          类别管理
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { HomeStatistics } from '@/types'

const router = useRouter()

// 统计数据
const statistics = ref<HomeStatistics>({
  todayCount: 0,
  monthCount: 0,
  totalCount: 0,
  pendingCount: 0,
  topCategory: {
    name: '',
    count: 0
  }
})

// 获取统计数据
async function fetchStatistics() {
  // TODO: 调用 API 获取统计数据
  // 这里先用模拟数据
  statistics.value = {
    todayCount: 12,
    monthCount: 156,
    totalCount: 1234,
    pendingCount: 23,
    topCategory: {
      name: '教学质量',
      count: 45
    }
  }
}

// 快捷操作
function goToFeedback() {
  router.push('/admin/feedback')
}

function goToStatistics() {
  router.push('/admin/statistics')
}

function goToCategory() {
  router.push('/admin/category')
}

// 初始化
onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.dashboard-page {
  max-width: 1400px;
  margin: 0 auto;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-icon.today {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.month {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #ffa751 0%, #ffe259 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.chart-card h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.top-category {
  text-align: center;
  padding: 40px 0;
}

.category-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.category-count {
  font-size: 16px;
  color: #666;
}

.action-card h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 12px;
}
</style>
