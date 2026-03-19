import axios from 'axios'
import { Message } from '@arco-design/web-vue'
import { storage } from './storage'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const token = storage.get<string>('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    Message.error(error.message || '请求失败')
    return Promise.reject(error)
  },
)

export default request
