import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category } from '@/types'
import { getCategoryList } from '@/api/category'

export const useFeedbackStore = defineStore('feedback', () => {
  // 状态
  const categories = ref<Category[]>([])
  const loading = ref(false)

  // 获取反馈类别列表
  async function fetchCategories() {
    if (categories.value.length > 0) {
      return categories.value
    }

    loading.value = true
    try {
      const res = await getCategoryList()
      categories.value = res.data
      return categories.value
    } catch (error) {
      console.error('获取类别列表失败：', error)
      return []
    } finally {
      loading.value = false
    }
  }

  // 根据 ID 获取类别
  function getCategoryById(id: number) {
    return categories.value.find((cat) => cat.id === id)
  }

  // 清空缓存
  function clearCache() {
    categories.value = []
  }

  return {
    categories,
    loading,
    fetchCategories,
    getCategoryById,
    clearCache
  }
})
