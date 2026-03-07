<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { userLogout } from '@/api/user'
import {
  Message,
  Menu,
  MenuItem,
  Avatar,
  Dropdown,
  Doption,
  Button,
  Space,
  InputSearch
} from '@arco-design/web-vue'
import {
  IconUser,
  IconNotification,
  IconApps,
  IconExport,
  IconSettings
} from '@arco-design/web-vue/es/icon'
import LoginModal from './LoginModal.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginModalVisible = ref(false)
const isLoggedIn = computed(() => !!userStore.token)
const isLoggingOut = ref(false)
const canAccessAdmin = computed(() => {
  const role = userStore.userInfo?.role
  return role === 'admin' || role === 'teacher'
})

// 菜单点击跳转
const handleMenuClick = (key: string) => {
  router.push(key)
}

const handleSelect = async (value: any) => {
  if (value === 'logout') {
    isLoggingOut.value = true
    try {
      const res = await userLogout()
      if (res.code === 200) {
        userStore.logout()
        Message.success('已安全退出')
        router.push('/')
      } else {
        Message.error(res.message || '退出失败')
      }
    } catch (error: any) {
      Message.error(error.message || '退出失败')
    } finally {
      isLoggingOut.value = false
    }
  } else if (value === 'admin') {
    router.push('/admin')
  } else {
    router.push(`/${value}`)
  }
}
</script>

<template>
  <div class="navbar-wrapper">
    <div class="navbar-content">
      <div class="left-side menu-side">
        <div class="logo-area" @click="router.push('/')">
          <span class="logo-text">AuraOJ</span>
        </div>
        <Menu mode="horizontal" :selected-keys="[route.path]" @menu-item-click="handleMenuClick" :border="false"
          class="nav-menu">
          <MenuItem key="/">主页</MenuItem>
          <MenuItem key="/problem">题目</MenuItem>
          <MenuItem key="/contest">训练赛</MenuItem>
          <MenuItem key="/status">评测状态</MenuItem>
          <MenuItem key="/rank">排名</MenuItem>
        </Menu>
      </div>

      <div class="right-side">
        <Space size="medium">
          <InputSearch placeholder="搜索题目..." style="width: 200px; background-color: var(--color-fill-2);"
            button-after />

          <Button type="text" class="icon-btn">
            <template #icon>
              <IconNotification />
            </template>
          </Button>

          <template v-if="isLoggedIn">
            <Dropdown trigger="hover" @select="handleSelect">
              <div class="user-avatar">
                <Avatar :size="32" :style="{ backgroundColor: '#165DFF' }">
                  <IconUser />
                </Avatar>
                <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
              </div>
              <template #content>
                <Doption value="profile">
                  <template #icon>
                    <IconUser />
                  </template>
                  个人设置
                </Doption>
                <Doption value="submission">
                  <template #icon>
                    <IconApps />
                  </template>
                  我的提交记录
                </Doption>
                <Doption v-if="canAccessAdmin" value="admin">
                  <template #icon>
                    <IconSettings />
                  </template>
                  管理后台
                </Doption>
                <hr style="border: none; border-top: 1px solid var(--color-fill-3); margin: 4px 0;" />
                <Doption value="logout" class="logout-option" :disabled="isLoggingOut">
                  <template #icon>
                    <IconExport />
                  </template>
                  退出登录
                </Doption>
              </template>
            </Dropdown>
          </template>

          <template v-else>
            <Button type="primary" shape="round" @click="loginModalVisible = true">
              登录 / 注册
            </Button>
          </template>
        </Space>
      </div>
    </div>

    <LoginModal v-model:visible="loginModalVisible" />
  </div>
</template>

<style scoped>
.navbar-wrapper {
  position: sticky;
  top: 0;
  z-index: 999;
  width: 100%;
  height: 60px;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--color-border-2);
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1440px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
}

.left-side {
  display: flex;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 32px;
}

.menu-side {
  flex: 1;
}

.logo-area img {
  width: 32px;
  height: 32px;
}

.logo-text {
  margin-left: 10px;
  font-size: 25px;
  font-weight: 900;
  font-family: "JetBrains Mono", "Fira Code", monospace;

  background: linear-gradient(90deg, #00c6ff, #0072ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;

  letter-spacing: 0.5px;

  text-shadow: 0 2px 8px rgba(0, 114, 255, 0.2);
}

.nav-menu {
  background: transparent;
}

.right-side {
  display: flex;
  align-items: center;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background-color 0.2s;
}

.user-avatar:hover {
  background-color: var(--color-fill-2);
}

.username {
  font-size: 14px;
  color: var(--color-text-2);
}

.icon-btn {
  color: var(--color-text-2);
  font-size: 18px;
}

.logout-option {
  color: var(--color-danger-light-4);
}
</style>
