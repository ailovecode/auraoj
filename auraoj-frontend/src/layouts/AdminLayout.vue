<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  Menu,
  MenuItem,
  MenuItemGroup,
  Button
} from '@arco-design/web-vue'
import {
  IconDashboard,
  IconUserGroup,
  IconFile,
  IconList,
  IconTrophy,
  IconSettings,
  IconExport,
  IconMenuFold,
  IconMenuUnfold,
  IconHome,
  IconCode
} from '@arco-design/web-vue/es/icon'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const handleMenuClick = (key: string) => {
  router.push(key)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const goToHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-sidebar" :class="{ 'is-collapsed': isCollapsed }">
      <div class="sidebar-header">
        <div v-if="!isCollapsed" class="sidebar-logo">AuraOJ Admin</div>
        <Button class="collapse-btn" type="text" @click="toggleCollapse">
          <template #icon>
            <IconMenuUnfold v-if="isCollapsed" />
            <IconMenuFold v-else />
          </template>
        </Button>
      </div>

      <Menu :selected-keys="[activeMenu]" @menu-item-click="handleMenuClick" class="admin-menu" theme="dark"
        :auto-open="true" :collapsed="isCollapsed">
        <MenuItemGroup title="管理">
          <MenuItem key="/admin/dashboard">
          <template #icon>
            <IconDashboard />
          </template>
          仪表盘
          </MenuItem>
          <MenuItem key="/admin/user">
          <template #icon>
            <IconUserGroup />
          </template>
          用户管理
          </MenuItem>
          <MenuItem key="/admin/problem">
          <template #icon>
            <IconFile />
          </template>
          题目管理
          </MenuItem>
          <MenuItem key="/admin/language">
          <template #icon>
            <IconCode />
          </template>
          语言管理
          </MenuItem>
          <MenuItem key="/admin/submission">
          <template #icon>
            <IconList />
          </template>
          提交记录
          </MenuItem>
          <MenuItem key="/admin/contest">
          <template #icon>
            <IconTrophy />
          </template>
          比赛管理
          </MenuItem>
        </MenuItemGroup>

        <MenuItemGroup title="系统">
          <MenuItem key="/admin/settings">
          <template #icon>
            <IconSettings />
          </template>
          系统设置
          </MenuItem>
        </MenuItemGroup>
      </Menu>

      <div class="sidebar-footer">
        <Button class="go-home-btn" @click="goToHome" type="primary" long v-if="!isCollapsed">
          <template #icon>
            <IconHome />
          </template>
          返回前台
        </Button>
        <Button class="go-home-btn" @click="goToHome" type="primary" v-else>
          <template #icon>
            <IconHome />
          </template>
        </Button>
        <div class="user-info">
          <div class="avatar-placeholder">
            {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'A' }}
          </div>
          <div v-if="!isCollapsed" class="user-meta">
            <div class="user-name">{{ userStore.userInfo?.username || 'Admin' }}</div>
            <div class="user-role">{{ userStore.userInfo?.role || 'TEACHER' }}</div>
          </div>
        </div>
        <Button class="logout-btn" @click="handleLogout" type="text">
          <template #icon>
            <IconExport />
          </template>
          <span v-if="!isCollapsed">退出登录</span>
        </Button>
      </div>
    </aside>

    <main class="admin-main">
      <div class="admin-content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

/* 侧边栏增加过渡动画 */
.admin-sidebar {
  width: 240px;
  background-color: #232324;
  display: flex;
  flex-direction: column;
  box-shadow: 1px 0 5px rgba(0, 0, 0, 0.1);
  z-index: 10;
  transition: width 0.3s cubic-bezier(0.34, 0.69, 0.1, 1);
}

/* 收缩状态下的侧边栏宽度 */
.admin-sidebar.is-collapsed {
  width: 64px;
}

/* 头部区域：展开时两端对齐，收缩时居中 */
.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.admin-sidebar.is-collapsed .sidebar-header {
  justify-content: center;
  padding: 0;
}

.sidebar-logo {
  font-size: 18px;
  font-weight: 800;
  font-family: "JetBrains Mono", "Fira Code", monospace;
  color: #fff;
  letter-spacing: 0.5px;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
}

/* 展开收缩按钮样式 */
.collapse-btn {
  color: #86909c;
  font-size: 18px;
  padding: 0 8px;
}

.collapse-btn:hover {
  background-color: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.admin-menu {
  flex: 1;
  background-color: transparent !important;
  padding: 8px;
}

.admin-menu :deep(.arco-menu-inner) {
  background-color: transparent !important;
  padding: 0;
}

/* 隐藏收缩状态下的分组标题 */
.admin-sidebar.is-collapsed :deep(.arco-menu-group-title) {
  display: none;
}

.admin-menu :deep(.arco-menu-group-title) {
  color: #86909c;
  font-size: 14px;
  margin-top: 16px;
  margin-bottom: 8px;
  padding-left: 16px;
  user-select: none;
}

.admin-menu :deep(.arco-menu-item) {
  background-color: transparent !important;
  color: #c9cdd4;
  margin: 8px 0;
  border-radius: 4px;
  transition: all 0.2s;
  height: 40px;
  line-height: 40px;
}

.admin-menu :deep(.arco-menu-item .arco-icon) {
  font-size: 16px;
  margin-right: 12px;
}

/* 收起状态时，清除图标的右边距使其居中 */
.admin-sidebar.is-collapsed :deep(.arco-menu-item .arco-icon) {
  margin-right: 0;
}

.admin-menu :deep(.arco-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.06) !important;
  color: #fff;
}

.admin-menu :deep(.arco-menu-item.arco-menu-selected) {
  background-color: rgba(255, 255, 255, 0.08) !important;
  color: #165dff;
  font-weight: 500;
}

.admin-menu :deep(.arco-menu-item.arco-menu-selected .arco-icon) {
  color: #165dff;
}

/* 底部用户信息区 */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.04);
}

.admin-sidebar.is-collapsed .sidebar-footer {
  padding: 16px 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
}

.admin-sidebar.is-collapsed .user-info {
  margin-bottom: 8px;
  padding: 0;
  justify-content: center;
}

.avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #165dff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  margin-right: 12px;
  flex-shrink: 0;
}

.admin-sidebar.is-collapsed .avatar-placeholder {
  margin-right: 0;
}

.user-meta {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #e5e6eb;
}

.user-role {
  font-size: 12px;
  color: #86909c;
  margin-top: 2px;
}

.logout-btn {
  width: 100%;
  justify-content: flex-start;
  color: #86909c;
  padding-left: 12px;
}

.admin-sidebar.is-collapsed .logout-btn {
  justify-content: center;
  padding-left: 0;
}

.logout-btn:hover {
  color: #f53f3f;
  background-color: rgba(245, 63, 63, 0.08);
}

.go-home-btn {
  margin-bottom: 12px;
}

.admin-main {
  flex: 1;
  background: #f2f3f5;
  height: 100vh;
  overflow-y: auto;
}

.admin-content {
  padding: 24px;
}
</style>
