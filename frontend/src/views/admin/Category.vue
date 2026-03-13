<template>
  <div class="category-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h3>类别管理</h3>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增类别
          </el-button>
        </div>
      </template>

      <!-- 类别列表 -->
      <el-table v-loading="loading" :data="categoryList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="类别名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="requireTeacher" label="是否必选教师" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.requireTeacher ? 'success' : 'info'">
              {{ row.requireTeacher ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="类别名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入类别名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="类别描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入类别描述（可选）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="是否必选教师" prop="requireTeacher">
          <el-switch
            v-model="formData.requireTeacher"
            active-text="是"
            inactive-text="否"
          />
          <div class="form-tip">
            开启后，学生提交该类别反馈时必须选择任课教师
          </div>
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="formData.sort"
            :min="0"
            :max="999"
            controls-position="right"
          />
          <div class="form-tip">
            数字越小越靠前
          </div>
        </el-form-item>
      </el-form>

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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import {
  getCategoryList,
  createCategory,
  updateCategory,
  deleteCategory
} from '@/api/category'
import type { Category, CreateCategoryRequest, UpdateCategoryRequest } from '@/types'

// 类别列表
const categoryList = ref<Category[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增类别')
const isEdit = ref(false)
const currentId = ref(0)

// 表单
const formRef = ref<FormInstance>()
const formData = reactive<CreateCategoryRequest>({
  name: '',
  description: '',
  requireTeacher: false,
  sort: 0
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入类别名称', trigger: 'blur' },
    { min: 2, max: 50, message: '类别名称长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 提交状态
const submitting = ref(false)

// 获取类别列表
async function fetchCategoryList() {
  loading.value = true
  try {
    const res = await getCategoryList()
    categoryList.value = res.data
  } catch (error) {
    console.error('获取类别列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 新增
function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增类别'
  resetForm()
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: Category) {
  isEdit.value = true
  dialogTitle.value = '编辑类别'
  currentId.value = row.id

  formData.name = row.name
  formData.description = row.description || ''
  formData.requireTeacher = row.requireTeacher
  formData.sort = row.sort

  dialogVisible.value = true
}

// 删除
async function handleDelete(row: Category) {
  try {
    await ElMessageBox.confirm(
      `确定要删除类别"${row.name}"吗？删除后该类别下的反馈将无法查看。`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    await fetchCategoryList()
  } catch (error) {
    // 用户取消或删除失败
  }
}

// 提交
async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true

    if (isEdit.value) {
      // 编辑
      const data: UpdateCategoryRequest = {
        id: currentId.value,
        ...formData
      }
      await updateCategory(data)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await createCategory(formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    await fetchCategoryList()
  } catch (error) {
    console.error('提交失败：', error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
function resetForm() {
  formData.name = ''
  formData.description = ''
  formData.requireTeacher = false
  formData.sort = 0
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  fetchCategoryList()
})
</script>

<style scoped>
.category-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.form-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}
</style>
