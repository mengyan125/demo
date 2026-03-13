<template>
  <div class="statistics-page">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-number">{{ todayCount }}</div>
        <div class="stat-label">今日收到学生反馈</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ totalCount }}</div>
        <div class="stat-label">本月共收到学生反馈</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ monthCategoryCount }}</div>
        <div class="stat-label">本月反馈类别最多是教学</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 左侧：类别统计 -->
      <div class="chart-section">
        <div class="section-title">
          <span class="title-bar"></span>
          <h3>反馈类别占比分布：</h3>
        </div>
        <div class="chart-wrapper">
          <div ref="categoryChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 右侧：趋势统计 -->
      <div class="chart-section">
        <div class="section-title">
          <span class="title-bar"></span>
          <h3>本学期反馈数量统计：</h3>
        </div>
        <div class="chart-wrapper">
          <div ref="trendChartRef" class="chart-container"></div>
        </div>
      </div>
    </div>

    <!-- 教师统计和数据导出 -->
    <div class="bottom-section">
      <!-- 教师统计 -->
      <div class="chart-section">
        <div class="section-title">
          <span class="title-bar"></span>
          <h3>教师反馈统计（Top 10）</h3>
        </div>
        <div class="table-header">
          <div class="date-range">
            <span class="label">时间范围：</span>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </div>
          <el-button type="primary" :loading="exporting" @click="handleExport" size="small">
            导出
          </el-button>
        </div>
        <div class="table-wrapper">
          <el-table :data="teacherTableData" stripe style="width: 100%" v-loading="teacherTableLoading">
            <el-table-column prop="rank" label="序号" min-width="60" align="center" />
            <el-table-column prop="grade" label="年级" min-width="80" align="center" />
            <el-table-column prop="class" label="班级" min-width="100" align="center" />
            <el-table-column prop="subject" label="课程" min-width="80" align="center" />
            <el-table-column prop="name" label="教师名称" min-width="100" align="center" />
            <el-table-column prop="count" label="被反馈次数" min-width="100" align="center" />
            <el-table-column prop="action" label="操作" min-width="80" align="center">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleViewTeacher(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  getStatisticsOverview,
  getCategoryStatistics,
  getTrendStatistics,
  getTeacherStatistics,
  exportTeacherStatistics
} from '@/api/statistics'

const router = useRouter()

// 图表实例
const categoryChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()

let categoryChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null

// 统计数据
const todayCount = ref(0)
const totalCount = ref(0)
const monthCategoryCount = ref('')

// 趋势类型
const trendType = ref('week')

// 教师统计表格数据
const teacherTableData = ref<any[]>([])
const teacherTableLoading = ref(false)

// 时间范围
const dateRange = ref<[string, string] | []>([])
const exporting = ref(false)

// 获取统计概览
async function fetchStatisticsOverview() {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      todayCount.value = res.data.todayCount || 0
      totalCount.value = res.data.monthCount || 0
      monthCategoryCount.value = res.data.monthCategoryCount || ''
    }
  } catch (error) {
    console.error('获取统计概览失败：', error)
    // 使用模拟数据
    todayCount.value = 24
    totalCount.value = 156
    monthCategoryCount.value = '教学质量'
  }
}

// 获取类别统计数据
async function fetchCategoryStatistics() {
  try {
    const res = await getCategoryStatistics()
    if (res.data && categoryChart) {
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [
          {
            name: '反馈类别',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: res.data
          }
        ]
      }
      categoryChart.setOption(option)
    }
  } catch (error) {
    console.error('获取类别统计失败：', error)
    // 使用模拟数据
    if (categoryChart) {
      const mockData = [
        { name: '教学质量', value: 45 },
        { name: '课程安排', value: 28 },
        { name: '师生关系', value: 18 },
        { name: '教学方法', value: 15 },
        { name: '其他', value: 12 }
      ]
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [
          {
            name: '反馈类别',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: mockData
          }
        ]
      }
      categoryChart.setOption(option)
    }
  }
}

// 获取趋势统计数据
async function fetchTrendStatistics() {
  try {
    const res = await getTrendStatistics({ type: trendType.value as 'week' | 'month' })
    if (res.data && trendChart) {
      updateTrendChart(res.data)
    }
  } catch (error) {
    console.error('获取趋势统计失败：', error)
  }
}

// 获取教师统计数据
async function fetchTeacherStatistics() {
  teacherTableLoading.value = true
  try {
    const params: any = {
      pageNum: 1,
      pageSize: 10
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getTeacherStatistics(params)
    if (res.data) {
      teacherTableData.value = res.data.list || res.data || []
    }
  } catch (error) {
    console.error('获取教师统计失败：', error)
    // 使用模拟数据
    teacherTableData.value = [
      { rank: 1, grade: '高一', class: '1班', subject: '数学', name: '张老师', count: 45, id: 1 },
      { rank: 2, grade: '高一', class: '2班', subject: '英语', name: '李老师', count: 38, id: 2 },
      { rank: 3, grade: '高二', class: '1班', subject: '物理', name: '王老师', count: 32, id: 3 },
      { rank: 4, grade: '高二', class: '2班', subject: '化学', name: '刘老师', count: 28, id: 4 },
      { rank: 5, grade: '高三', class: '1班', subject: '语文', name: '陈老师', count: 25, id: 5 },
      { rank: 6, grade: '高三', class: '2班', subject: '历史', name: '杨老师', count: 22, id: 6 },
      { rank: 7, grade: '高一', class: '3班', subject: '地理', name: '赵老师', count: 19, id: 7 },
      { rank: 8, grade: '高二', class: '3班', subject: '生物', name: '黄老师', count: 16, id: 8 },
      { rank: 9, grade: '高三', class: '3班', subject: '政治', name: '周老师', count: 14, id: 9 },
      { rank: 10, grade: '高一', class: '4班', subject: '体育', name: '吴老师', count: 12, id: 10 }
    ]
    ElMessage.warning('使用模拟数据展示')
  } finally {
    teacherTableLoading.value = false
  }
}

// 初始化类别统计图表
function initCategoryChart() {
  if (!categoryChartRef.value) return

  categoryChart = echarts.init(categoryChartRef.value)
  // 初始化空图表，数据通过 fetchCategoryStatistics 加载
  categoryChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '反馈类别',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: []
      }
    ]
  })
}

// 初始化趋势统计图表
function initTrendChart() {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  updateTrendChart()
}

// 更新趋势图表
function updateTrendChart(data?: any[]) {
  if (!trendChart) return

  // 如果没有传入数据，使用 Mock 数据
  let dates: string[] = []
  let chartData: number[] = []

  if (data && data.length > 0) {
    dates = data.map((item) => item.date)
    chartData = data.map((item) => item.count)
  } else {
    dates =
      trendType.value === 'week'
        ? ['03-05', '03-06', '03-07', '03-08', '03-09', '03-10', '03-11']
        : Array.from({ length: 30 }, (_, i) => {
            const date = new Date()
            date.setDate(date.getDate() - 29 + i)
            return `${date.getMonth() + 1}-${date.getDate()}`
          })

    chartData =
      trendType.value === 'week'
        ? [12, 15, 18, 14, 20, 16, 19]
        : Array.from({ length: 30 }, () => Math.floor(Math.random() * 20) + 10)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '反馈数量'
    },
    series: [
      {
        name: '反馈数量',
        type: 'bar',
        data: chartData,
        barWidth: '40%',
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#409eff' },
              { offset: 1, color: '#66b1ff' }
            ]
          }
        }
      }
    ]
  }

  trendChart.setOption(option)
}

// 初始化教师统计图表
function initTeacherChart() {
  if (!teacherChartRef.value) return

  teacherChart = echarts.init(teacherChartRef.value)

  // Mock 数据
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '反馈数量'
    },
    yAxis: {
      type: 'category',
      data: ['张老师', '李老师', '王老师', '刘老师', '陈老师', '杨老师', '赵老师', '黄老师', '周老师', '吴老师']
    },
    series: [
      {
        name: '反馈数量',
        type: 'bar',
        data: [45, 38, 32, 28, 25, 22, 19, 16, 14, 12],
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]
          }
        }
      }
    ]
  }

  teacherChart.setOption(option)
}

// 趋势类型变化
function handleTrendTypeChange() {
  fetchTrendStatistics()
}

// 导出数据
async function handleExport() {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('请选择时间范围')
    return
  }

  exporting.value = true
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    const blob = await exportTeacherStatistics(params)
    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `教师反馈统计_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败：', error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// 查看教师详情
function handleViewTeacher(row: any) {
  router.push({
    name: 'TeacherDetail',
    params: { teacherId: row.id || row.rank }
  })
}

// 窗口大小变化时重新渲染图表
function handleResize() {
  categoryChart?.resize()
  trendChart?.resize()
  teacherChart?.resize()
}

// 监听时间范围变化，重新加载表格数据
watch(dateRange, () => {
  fetchTeacherStatistics()
})

// 初始化
onMounted(() => {
  initCategoryChart()
  initTrendChart()

  // 获取所有统计数据
  fetchStatisticsOverview()
  fetchCategoryStatistics()
  fetchTrendStatistics()
  fetchTeacherStatistics()

  window.addEventListener('resize', handleResize)
})

// 清理
onUnmounted(() => {
  categoryChart?.dispose()
  trendChart?.dispose()

  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.statistics-page {
  width: 100%;
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0;
  margin-bottom: 0;
  padding: 40px 20px;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.stat-card {
  text-align: center;
  padding: 20px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
}

.stat-card:last-child {
  border-right: none;
}

.stat-number {
  font-size: 48px;
  font-weight: bold;
  color: #ff9900;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 图表容器 */
.charts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  padding: 0;
  flex-shrink: 0;
  height: 300px;
}

.chart-section {
  background: #fff;
  border: none;
  border-right: 1px solid #e8e8e8;
  border-bottom: 1px solid #e8e8e8;
  border-radius: 0;
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.charts-container .chart-section:nth-child(2) {
  border-right: none;
}

.chart-section-full {
  border-right: none !important;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
  height: auto;
}

.bottom-section {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: 0;
  flex: 1;
  min-height: 0;
  border-bottom: 1px solid #e8e8e8;
}

.bottom-section .chart-section {
  border-right: none;
  border-bottom: none;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.bottom-section .chart-section:last-child {
  border-bottom: none;
}

.export-section {
  flex-shrink: 0;
  height: auto;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.title-bar {
  display: inline-block;
  width: 4px;
  height: 20px;
  background: #ff3333;
  margin-right: 10px;
  border-radius: 0;
}

.section-title h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.chart-wrapper {
  width: 100%;
  height: 100%;
}

.chart-container {
  width: 100%;
  height: 100%;
}

.export-content {
  padding: 20px 0;
}

.export-label {
  margin-right: 10px;
  color: #333;
}

.export-separator {
  margin: 0 10px;
  color: #333;
}

.table-wrapper {
  width: 100%;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 0 16px 0;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-range .label {
  color: #333;
  font-weight: 500;
}

.separator {
  color: #333;
  margin: 0 8px;
}
</style>
