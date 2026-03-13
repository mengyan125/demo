<template>
  <div class="permission-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h3>权限配置</h3>
          <p>为管理员分配可查看的反馈类别权限</p>
        </div>
      </template>

      <!-- 管理员列表 -->
      <el-table v-loading="loading" :data="adminList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="role" label="角色" width="150">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categories" label="可查看类别" min-width="300">
          <template #default="{ row }">
            <el-tag
              v-for="category in row.categories"
              :key="category.id"
              style="margin-right: 8px"
              size="small"
            >
              {{ category.name }}
            </el-tag>
            <span v-if="row.categories.length === 0" class="no-permission">暂无权限</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleAssign(row)">
              分配权限
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="分配权限"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentAdmin" class="dialog-content">
        <el-alert
          :title="`正在为 ${currentAdmin.realName}（${getRoleText(currentAdmin.role)}）分配权限`"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        />

        <el-form label-width="100px">
          <el-form-item label="可查看类别">
            <el-checkbox-group v-model="selectedCategories">
              <el-checkbox
                v-for="category in allCategories"
                :key="category.id"
                :label="category.id"
                style="display: block; margin-bottom: 12px"
              >
                {{ category.name }}
                <span v-if="category.description" class="category-desc">
                  （{{ category.description }}）
                </span>
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useFeedbackStore } from '@/stores/feedback'
import type { User, Category, UserRole } from '@/types'

const feedbackStore = useFeedbackStore()

// 管理员列表
interface AdminWithCategories extends User {
  categories: Category[]
}

const adminList = ref<AdminWithCategories[]>([])
const loading = ref(false)

// 所有类别
const allCategories = ref<Category[]>([])

// 对话框
const dialogVisible = ref(false)
const currentAdmin = ref<AdminWithCategories | null>(null)
const selectedCategories = ref<number[]>([])
const submitting = ref(false)

// 获取管理员列表
async function fetchAdminList() {
  loading.value = true
  try {
    // TODO: 调用 API 获取管理员列表
    // Mock 数据
    await new Promise((resolve) => setTimeout(resolve, 500))

    adminList.value = [
      {
        id: 2,
        username: 'admin1',
        realName: '李老师',
        role: 'category_admin',
        createTime: '2024-01-01',
        categories: [
          { id: 1, name: '教学质量', requireTeacher: true, sort: 1, createTime: '2024-01-01' },
          { id: 2, name: '校园设施', requireTeacher: false, sort: 2, createTime: '2024-01-01' }
        ]
      },
      {
        id: 3,
        username: 'admin2',
        realName: '王老师',
        role: 'category_admin',
        createTime: '2024-01-01',
        categories: []
      },
      {
        id: 4,
        username: 'leader1',
        realName: '张主任',
        role: 'dept_leader',
        createTime: '2024-01-01',
        categories: [
          { id: 1, name: '教学质量', requireTeacher: true, sort: 1, createTime: '2024-01-01' }
        ]
      }
    ]
  } catch (error) {
    console.error('获取管理员列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 分配权限
function handleAssign(admin: AdminWithCategories) {
  currentAdmin.value = admin
  selectedCategories.value = admin.categories.map((c) => c.id)
  dialogVisible.value = true
}

// 提交
async function handleSubmit() {
  if (!currentAdmin.value) return

  submitting.value = true
  try {
    // TODO: 调用 API 分配权限
    await new Promise((resolve) => setTimeout(resolve, 500))

    ElMessage.success('权限分配成功')
    dialogVisible.value = false

    // 刷新列表
    await fetchAdminList()
  } catch (error) {
    console.error('分配权限失败：', error)
    ElMessage.error('分配权限失败')
  } finally {
    submitting.value = false
  }
}

// 获取角色类型
function getRoleType(role: UserRole) {
  const typeMap = {
    student: 'info',
    category_admin: 'success',
    dept_leader: 'warning',
    school_leader: 'danger',
    system_admin: 'danger'
  }
  return typeMap[role] || 'info'
}

// 获取角色文本
function getRoleText(role: UserRole) {
  const textMap = {
    student: '学生',
    category_admin: '类别管理员',
    dept_leader: '部门领导',
    school_leader: '校领导',
    system_admin: '系统管理员'
  }
  return textMap[role] || role
}

// 初始化
onMounted(async () => {
  await feedbackStore.fetchCategories()
  allCategories.value = feedbackStore.categories
  await fetchAdminList()
})
</script>

<style scoped>
.permission-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.no-permission {
  color: #999;
  font-size: 14px;
}

.dialog-content {
  padding: 10px 0;
}

.category-desc {
  font-size: 12px;
  color: #999;
}
</style>
