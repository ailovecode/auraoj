import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import HomeViews from '@/views/home/HomeViews.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL || '/'),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: HomeViews
        },
        {
          path: 'problem',
          name: 'problem',
          component: {
            template: '<div class="placeholder-page"><h1>题目列表</h1></div>'
          }
        },
        {
          path: 'submission',
          name: 'submission',
          component: {
            template: '<div class="placeholder-page"><h1>提交记录</h1></div>'
          }
        },
        {
          path: 'rank',
          name: 'rank',
          component: {
            template: '<div class="placeholder-page"><h1>排行榜</h1></div>'
          }
        },
        {
          path: 'login',
          name: 'login',
          component: {
            template: '<div class="placeholder-page"><h1>登录</h1></div>'
          }
        }
      ]
    }
  ]
})

export default router
