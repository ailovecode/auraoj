<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { userLogin, userRegister } from '@/api/user'
import { Modal, Form, Input, Button, Message, FormItem, Select, Option } from '@arco-design/web-vue'
import { IconUser, IconLock } from '@arco-design/web-vue/es/icon'

interface Props {
  visible: boolean
}

interface Emits {
  (e: 'update:visible', value: boolean): void
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const userStore = useUserStore()
const loading = ref(false)

// 登录表单
const loginFormRef = ref()
const loginData = ref({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

// 注册表单
const registerModalVisible = ref(false)
const registerFormRef = ref()
const registerLoading = ref(false)
const registerData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  gender: undefined as number | undefined,
  school: ''
})

const genderOptions = [
  { label: '女', value: 0 },
  { label: '男', value: 1 }
]

const registerRules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少 6 位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    {
      validator: (value: string, callback: (error?: string) => void) => {
        if (value !== registerData.password) {
          callback('两次输入的密码不一致')
        } else {
          callback()
        }
      }
    }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true

    const res = await userLogin({
      username: loginData.value.username,
      password: loginData.value.password
    })

    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUserInfo(res.data)
      Message.success('登录成功')
      emit('update:visible', false)
      loginData.value = { username: '', password: '' }
    } else {
      Message.error(res.message || '登录失败')
    }
  } catch (error: any) {
    if (error?.response?.data?.message) {
      Message.error(error.response.data.message)
    } else if (error?.message) {
      Message.error(error.message)
    } else {
      Message.error('登录失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('update:visible', false)
  loginData.value = { username: '', password: '' }
}

const handleRegisterClick = () => {
  registerModalVisible.value = true
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    registerLoading.value = true

    const res = await userRegister({
      username: registerData.username,
      password: registerData.password,
      confirmPassword: registerData.confirmPassword,
      gender: registerData.gender,
      school: registerData.school || undefined
    })

    if (res.code === 200) {
      Message.success('注册成功，请登录')
      registerModalVisible.value = false
      resetRegisterForm()
    } else {
      Message.error(res.message || '注册失败')
    }
  } catch (error: any) {
    if (error?.response?.data?.message) {
      Message.error(error.response.data.message)
    } else if (error?.message) {
      Message.error(error.message)
    } else {
      Message.error('注册失败，请稍后重试')
    }
  } finally {
    registerLoading.value = false
  }
}

const handleRegisterCancel = () => {
  registerModalVisible.value = false
  resetRegisterForm()
}

const resetRegisterForm = () => {
  registerData.username = ''
  registerData.password = ''
  registerData.confirmPassword = ''
  registerData.gender = undefined
  registerData.school = ''
}
</script>

<template>
  <div>
    <Modal :visible="visible" title="登录 AuraOJ" @ok="handleLogin" @cancel="handleCancel" :footer="false"
      :closable="false" :mask-closable="false">
      <Form ref="loginFormRef" :model="loginData" :rules="loginRules">
        <FormItem field="username" label="用户名">
          <Input v-model="loginData.username" placeholder="请输入用户名/邮箱/手机号" :prefix-icon="IconUser" size="large" />
        </FormItem>
        <FormItem field="password" label="密码">
          <Input v-model="loginData.password" type="password" placeholder="请输入密码" :prefix-icon="IconLock" size="large"
            @press-enter="handleLogin" />
        </FormItem>
        <div style="margin-top: 24px">
          <Button type="primary" long size="large" :loading="loading" @click="handleLogin">
            登录
          </Button>
          <div style="text-align: center; margin-top: 16px">
            <span style="color: #4e5969">还没有账号？</span>
            <a style="color: #165dff; cursor: pointer" @click="handleRegisterClick">
              立即注册
            </a>
          </div>
        </div>
      </Form>
    </Modal>

    <Modal :visible="registerModalVisible" title="注册 AuraOJ" @ok="handleRegister" @cancel="handleRegisterCancel"
      :footer="false" :closable="false" :mask-closable="false" width="480">
      <Form ref="registerFormRef" :model="registerData" :rules="registerRules" layout="vertical">
        <FormItem field="username" label="用户名">
          <Input v-model="registerData.username" placeholder="请输入用户名（3-20个字符）" size="large" />
        </FormItem>
        <FormItem field="password" label="密码">
          <Input v-model="registerData.password" type="password" placeholder="请输入密码（至少6位）" size="large" />
        </FormItem>
        <FormItem field="confirmPassword" label="确认密码">
          <Input v-model="registerData.confirmPassword" type="password" placeholder="请再次输入密码" size="large" />
        </FormItem>
        <FormItem field="gender" label="性别">
          <Select v-model="registerData.gender" placeholder="请选择性别（选填）" size="large">
            <Option v-for="option in genderOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </Option>
          </Select>
        </FormItem>
        <FormItem field="school" label="学校/机构">
          <Input v-model="registerData.school" placeholder="请输入学校/机构（选填）" size="large" />
        </FormItem>
        <div style="margin-top: 24px">
          <Button type="primary" long size="large" :loading="registerLoading" @click="handleRegister">
            注册
          </Button>
          <div style="text-align: center; margin-top: 16px">
            <span style="color: #4e5969">已有账号？</span>
            <a style="color: #165dff; cursor: pointer" @click="handleRegisterCancel">
              立即登录
            </a>
          </div>
        </div>
      </Form>
    </Modal>
  </div>
</template>

<style scoped></style>
