import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Message } from '@arco-design/web-vue'
import MainLayout from '@/layouts/MainLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
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
          component: () => import('@/views/home/ProblemList.vue')
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
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/home/Profile.vue')
        },
        {
          path: 'login',
          name: 'login',
          component: {
            template: '<div class="placeholder-page"><h1>登录</h1></div>'
          }
        }
      ]
    },
    {
      path: '/admin',
      component: AdminLayout,
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'adminDashboard',
          component: () => import('@/views/admin/Dashboard.vue')
        },
        {
          path: 'user',
          name: 'adminUser',
          component: () => import('@/views/admin/UserManage.vue')
        },
        {
          path: 'problem',
          name: 'adminProblem',
          component: () => import('@/views/admin/ProblemManage.vue')
        },
        {
          path: 'problem/add',
          name: 'adminAddProblem',
          component: () => import('@/views/admin/AddProblem.vue')
        },
        {
          path: 'submission',
          name: 'adminSubmission',
          component: {
            template: '<div class="placeholder-page"><h1>提交记录</h1></div>'
          }
        },
        {
          path: 'contest',
          name: 'adminContest',
          component: {
            template: '<div class="placeholder-page"><h1>比赛管理</h1></div>'
          }
        },
        {
          path: 'settings',
          name: 'adminSettings',
          component: {
            template: '<div class="placeholder-page"><h1>系统设置</h1></div>'
          }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.path.startsWith('/admin')) {
    const role = userStore.userInfo?.role
    const isAdmin = role === 'admin' || role === 'teacher'

    if (!isAdmin) {
      Message.warning('您没有权限访问管理后台')
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
