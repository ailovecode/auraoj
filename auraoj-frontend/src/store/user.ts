import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types/user'
import { storage } from '@/utils/storage'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(storage.get<string>('token', ''))
  const userInfo = ref<UserInfo | null>(storage.get<UserInfo>('userInfo'))

  const setToken = (newToken: string) => {
    token.value = newToken
    storage.set('token', newToken)
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    storage.set('userInfo', info)
  }

  const isAdmin = computed(() => {
    const role = userInfo.value?.role
    return role === 'admin' || role === 'teacher'
  })

  const logout = () => {
    token.value = ''
    userInfo.value = null
    storage.remove('token')
    storage.remove('userInfo')
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    isAdmin,
    logout
  }
})
