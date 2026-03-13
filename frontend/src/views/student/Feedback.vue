<template>
  <div class="feedback-page">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">意见反馈</span>
        </div>
        <div class="nav-links">
          <router-link to="/student/feedback" class="nav-link router-link-active">我要反馈</router-link>
          <router-link to="/student/track" class="nav-link">反馈跟踪</router-link>
        </div>
        <div class="nav-right">
          <el-icon><Setting /></el-icon>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="content-wrapper">
      <div class="form-container">
        <el-form
          ref="feedbackFormRef"
          :model="feedbackForm"
          :rules="feedbackRules"
          class="feedback-form"
        >
          <!-- 反馈类别 -->
          <div class="form-row">
            <label class="form-label">
              <span class="required">*</span>反馈类别：
            </label>
            <div class="form-control">
              <el-select
                v-model="feedbackForm.categoryId"
                placeholder="请选择反馈类别"
                @change="handleCategoryChange"
              />
            </div>
          </div>

          <!-- 与任课教师相关 -->
          <div v-if="selectedCategory?.requireTeacher" class="form-row">
            <label class="form-label">与任课教师相关</label>
            <div class="form-control">
              <el-radio-group v-model="feedbackForm.isTeacherRelated">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </div>
          </div>

          <!-- 反馈对象 -->
          <div v-if="feedbackForm.isTeacherRelated" class="form-row">
            <label class="form-label">
              <span class="required">*</span>反馈对象：
            </label>
            <div class="form-control">
              <el-select
                v-model="feedbackForm.teacherId"
                placeholder="请选择任课教师"
                filterable
              />
            </div>
          </div>

          <!-- 反馈主题 -->
          <div class="form-row">
            <label class="form-label">
              <span class="required">*</span>反馈主题：
            </label>
            <div class="form-control">
              <el-input
                v-model="feedbackForm.title"
                placeholder="请输入"
                maxlength="20"
                show-word-limit
              />
              <div class="form-error">请输入反馈主题！</div>
            </div>
          </div>

          <!-- 反馈内容 -->
          <div class="form-row">
            <label class="form-label">
              <span class="required">*</span>反馈内容：
            </label>
            <div class="form-control">
              <el-input
                v-model="feedbackForm.content"
                type="textarea"
                :rows="6"
                placeholder="亲爱的同学你好，你反馈的内容我们已上传与任课老师无法查看，只有学校领导才能查看，我们将严格为你保密，请放心反馈！"
                maxlength="500"
                show-word-limit
              />
              <div class="form-error">请输入反馈内容！</div>
            </div>
          </div>

          <!-- 匿名反馈 -->
          <div class="form-row">
            <label class="form-label">匿名反馈</label>
            <div class="form-control">
              <el-radio-group v-model="feedbackForm.isAnonymous">
                <el-radio :label="false">实名反馈</el-radio>
                <el-radio :label="true">匿名反馈（选择匿名后，不显示反馈人姓名）</el-radio>
              </el-radio-group>
            </div>
          </div>

          <!-- 附件上传 -->
          <div class="form-row">
            <label class="form-label">附件：</label>
            <div class="form-control">
              <el-upload
                v-model:file-list="fileList"
                :auto-upload="false"
                :limit="9"
                :on-exceed="handleExceed"
                :before-upload="beforeUpload"
                action="#"
              >
                <el-button type="primary">点击上传</el-button>
              </el-upload>
              <div class="upload-tip">
                只能上传图片和视频，且不超过25M
              </div>
            </div>
          </div>

          <!-- 按钮 -->
          <div class="form-row button-row">
            <div class="form-label"></div>
            <div class="form-control button-group">
              <el-button class="btn-save" @click="handleSaveDraft">暂存</el-button>
              <el-button type="primary" class="btn-submit" :loading="submitting" @click="handleSubmit">
                提交
              </el-button>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules, UploadUserFile } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import { useFeedbackStore } from '@/stores/feedback'
import { getTeacherList } from '@/api/user'
import { submitFeedback } from '@/api/feedback'
import { uploadFiles } from '@/api/upload'
import type { Teacher } from '@/types'

const router = useRouter()
const feedbackStore = useFeedbackStore()

// 表单引用
const feedbackFormRef = ref<FormInstance>()

// 反馈表单数据
const feedbackForm = reactive<any>({
  categoryId: 0,
  isTeacherRelated: false,
  teacherId: undefined,
  title: '',
  content: '',
  isAnonymous: false,
  attachments: []
})

// 表单验证规则
const feedbackRules: FormRules = {
  categoryId: [{ required: true, message: '请选择反馈类别', trigger: 'change' }],
  title: [{ required: true, message: '请输入反馈主题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}

// 类别列表
const categories = computed(() => feedbackStore.categories)

// 选中的类别
const selectedCategory = computed(() => {
  return feedbackStore.getCategoryById(feedbackForm.categoryId)
})

// 教师列表
const teachers = ref<Teacher[]>([])

// 文件列表
const fileList = ref<UploadUserFile[]>([])

// 提交状态
const submitting = ref(false)

// 获取教师列表
async function fetchTeachers() {
  try {
    const res = await getTeacherList()
    teachers.value = res.data
  } catch (error) {
    console.error('获取教师列表失败：', error)
  }
}

// 处理类别变化
function handleCategoryChange() {
  // 如果新类别不需要选择教师，清空相关字段
  if (!selectedCategory.value?.requireTeacher) {
    feedbackForm.isTeacherRelated = false
    feedbackForm.teacherId = undefined
  }
}

// 文件超出限制
function handleExceed() {
  ElMessage.warning('最多只能上传9个文件')
}

// 文件上传前检查
function beforeUpload(file: File) {
  const isLt25M = file.size / 1024 / 1024 < 25

  if (!isLt25M) {
    ElMessage.error('文件大小不能超过 25MB')
    return false
  }

  return true
}

// 暂存草稿
function handleSaveDraft() {
  ElMessage.success('草稿已保存')
}

// 提交反馈
async function handleSubmit() {
  if (!feedbackFormRef.value) return

  try {
    // 验证表单
    await feedbackFormRef.value.validate()

    submitting.value = true

    // 上传附件
    if (fileList.value.length > 0) {
      const files = fileList.value.map((item) => item.raw as File)
      const uploadRes = await uploadFiles(files)
      feedbackForm.attachments = uploadRes.data.map((item: { url: string }) => item.url)
    }

    // 提交反馈
    await submitFeedback(feedbackForm)

    ElMessage.success('反馈提交成功')

    // 跳转到反馈跟踪页面
    router.push('/student/track')
  } catch (error) {
    console.error('提交反馈失败：', error)
  } finally {
    submitting.value = false
  }
}

// 初始化
onMounted(async () => {
  await feedbackStore.fetchCategories()
  await fetchTeachers()
})
</script>

<style scoped>
.feedback-page {
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

.form-container {
  background: white;
  border-radius: 4px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 表单 */
.feedback-form {
  margin-top: 0;
}

.form-row {
  margin-bottom: 24px;
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #333;
  font-weight: 500;
  text-align: right;
  min-width: 120px;
  padding-top: 8px;
  flex-shrink: 0;
}

.required {
  color: #f56c6c;
  margin-right: 4px;
}

.form-control {
  flex: 1;
  min-width: 0;
}

.form-control :deep(.el-select),
.form-control :deep(.el-input),
.form-control :deep(.el-input__wrapper),
.form-control :deep(.el-radio-group) {
  width: 100%;
}

.form-error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  display: none;
}

/* 上传区域 */
.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

/* 按钮 */
.button-row {
  margin-top: 30px;
}

.button-group {
  display: flex;
  gap: 12px;
}

.btn-save,
.btn-submit {
  min-width: 100px;
  height: 36px;
  font-size: 14px;
}

.btn-save {
  border: 1px solid #ddd;
  color: #333;
  background: white;
}

.btn-save:hover {
  border-color: #999;
  color: #333;
}

.btn-submit {
  background: #0099cc;
  border-color: #0099cc;
}

.btn-submit:hover {
  background: #0077aa;
  border-color: #0077aa;
}
</style>
