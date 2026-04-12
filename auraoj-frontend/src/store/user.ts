import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types/user'
import { storage } from '@/utils/storage'
import request from '@/utils/request'

const TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000 // Token 24小时过期

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(storage.getNoExpire<string>('token') || '')
  const userInfo = ref<UserInfo | null>(storage.get<UserInfo>('userInfo'))
  const showLoginModal = ref(false)
  const tokenExpireAt = ref<number>(storage.getNoExpire<number>('tokenExpireAt') || 0)

  const hasValidToken = computed(() => {
    if (!token.value) return false
    if (tokenExpireAt.value && Date.now() > tokenExpireAt.value) {
      return false
    }
    return true
  })

  const setToken = (newToken: string, expireIn?: number) => {
    token.value = newToken
    storage.setNoExpire('token', newToken)

    // 设置过期时间
    const expireAt = expireIn ? Date.now() + expireIn : Date.now() + TOKEN_EXPIRE_TIME
    tokenExpireAt.value = expireAt
    storage.setNoExpire('tokenExpireAt', expireAt)
  }

  const setUserInfo = (info: UserInfo, expireIn?: number) => {
    userInfo.value = info
    // 用户信息24小时过期
    storage.set('userInfo', info, expireIn || 24 * 60 * 60 * 1000)
  }

  const isAdmin = computed(() => {
    const role = userInfo.value?.role
    return role === 'admin' || role === 'teacher'
  })

  const logout = () => {
    token.value = ''
    userInfo.value = null
    tokenExpireAt.value = 0
    storage.remove('token')
    storage.remove('userInfo')
    storage.remove('tokenExpireAt')
  }

  const requireLogin = () => {
    logout()
    showLoginModal.value = true
  }

  /**
   * 验证当前用户状态是否有效
   * 在应用启动时或路由切换时调用
   */
  const validateUserStatus = async () => {
    // 如果没有 token 或已过期，直接返回
    if (!hasValidToken.value) {
      logout()
      return false
    }

    // 如果有 token 但没有用户信息，尝试获取
    if (token.value && !userInfo.value) {
      try {
        const res = await request.get('/user/info')
        if (res.code === 200 && res.data) {
          setUserInfo(res.data)
          return true
        } else {
          // 获取用户信息失败，说明 token 可能无效
          logout()
          return false
        }
      } catch (error) {
        console.error('验证用户状态失败:', error)
        logout()
        return false
      }
    }

    // 如果 token 过期，清除状态
    if (tokenExpireAt.value && Date.now() > tokenExpireAt.value) {
      logout()
      return false
    }

    return true
  }

  /**
   * 处理认证失败
   * 在后端返回 401 等认证错误时调用
   */
  const handleAuthFailure = (message?: string) => {
    logout()
    showLoginModal.value = true
    if (message) {
      console.warn('认证失败:', message)
    }
  }

  return {
    token,
    userInfo,
    showLoginModal,
    tokenExpireAt,
    hasValidToken,
    setToken,
    setUserInfo,
    isAdmin,
    logout,
    requireLogin,
    validateUserStatus,
    handleAuthFailure
  }
})
