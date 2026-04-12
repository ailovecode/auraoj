import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useUserStore } from './store/user'

import ArcoVue from '@arco-design/web-vue'
import '@arco-design/web-vue/dist/arco.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ArcoVue)

// 应用启动时验证用户状态
const initApp = async () => {
  const userStore = useUserStore()

  // 清理过期的存储数据
  userStore.validateUserStatus()
}

// 在路由准备完成后验证用户状态
router.isReady().then(() => {
  initApp()
})

app.mount('#app')
