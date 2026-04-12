import axios from 'axios'
import { Message } from '@arco-design/web-vue'
import { storage } from './storage'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 标记是否正在刷新 token，避免重复刷新
let isRefreshing = false
// 存储等待刷新完成的请求队列
let subscribers: Array<(token: string) => void> = []

// 添加请求拦截器
request.interceptors.request.use(
  (config) => {
    // 清理过期的存储数据
    storage.cleanExpired()

    // 从 localStorage 获取 token
    const token = storage.getNoExpire<string>('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 添加响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  async (error) => {
    const originalRequest = error.config

    // 处理 401 未授权错误
    if (error.response?.status === 401 || error.response?.data?.code === 401) {
      // 如果不是刷新 token 的请求
      if (!originalRequest.url?.includes('/user/refresh')) {
        // 清除本地存储的用户信息
        storage.remove('token')
        storage.remove('userInfo')
        storage.remove('tokenExpireAt')

        // 提示用户
        Message.error({
          id: 'auth-error',
          content: '登录已过期，请重新登录',
        })

        // 可以选择跳转到登录页或打开登录弹窗
        // window.location.href = '/'
      }
    }

    // 处理 403 禁止访问错误
    if (error.response?.status === 403 || error.response?.data?.code === 403) {
      Message.error({
        id: 'auth-error',
        content: '您没有权限访问该资源',
      })
    }

    // 处理网络错误
    if (!error.response) {
      Message.error({
        id: 'network-error',
        content: '网络连接失败，请检查网络',
      })
    }

    return Promise.reject(error)
  },
)

export default request
