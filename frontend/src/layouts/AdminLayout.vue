<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-left">
        <h1 class="logo">学生反馈系统 - 管理端</h1>
      </div>
      <div class="header-center">
        <el-menu :default-active="activeMenu" router mode="horizontal" class="top-menu">
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>首页统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/feedback">
            <el-icon><List /></el-icon>
            <span>反馈查看</span>
          </el-menu-item>
          <el-menu-item index="/admin/statistics">
            <el-icon><TrendCharts /></el-icon>
            <span>反馈统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/category">
            <el-icon><Folder /></el-icon>
            <span>类别管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/permission">
            <el-icon><Setting /></el-icon>
            <span>权限配置</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <span class="user-name">{{ userStore.userInfo?.realName }}</span>
        <el-dropdown @command="handleCommand">
          <el-avatar :size="36" :src="userStore.userInfo?.avatar">
            {{ userStore.userInfo?.realName?.charAt(0) }}
          </el-avatar>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <el-container class="layout-container">
      <!-- 内容区域 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 处理下拉菜单命令
async function handleCommand(command: string) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await userStore.logout()
      router.push('/login')
    } catch (error) {
      // 用户取消
    }
  }
}
</script>

<style scoped>
.admin-layout {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background: #0099cc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 60px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  min-width: 200px;
}

.logo {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.top-menu {
  background: transparent;
  border: none;
  display: flex;
  justify-content: center;
  gap: 0;
}

.top-menu :deep(.el-menu-item) {
  color: #fff;
  background: transparent;
  border: none;
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
}

.top-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.top-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border-bottom: 3px solid #fff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 150px;
  justify-content: flex-end;
}

.user-name {
  font-size: 14px;
  color: #fff;
}

.layout-container {
  flex: 1;
  overflow: hidden;
}

.layout-main {
  background: #fff;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden;
  height: 100%;
}
</style>
