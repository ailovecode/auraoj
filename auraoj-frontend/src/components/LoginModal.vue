<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { userLogin } from '@/api/user'
import { Modal, Form, Input, Button, Message } from '@arco-design/web-vue'
import { IconUser, IconLock } from '@arco-design/web-vue/es/icon'

interface Props {
  visible: boolean
}

interface Emits {
  (e: 'update:visible', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const formData = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

const handleOk = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const res = await userLogin({
      username: formData.value.username,
      password: formData.value.password
    })
    
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUserInfo(res.data)
      Message.success('登录成功')
      emit('update:visible', false)
      formData.value = { username: '', password: '' }
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
  formData.value = { username: '', password: '' }
}

const handleRegister = () => {
  Message.info('注册功能开发中')
}
</script>

<template>
  <Modal
    :visible="visible"
    title="登录 AuraOJ"
    @ok="handleOk"
    @cancel="handleCancel"
    :footer="false"
    :closable="false"
    :mask-closable="false"
  >
    <Form ref="formRef" :model="formData" :rules="rules">
      <FormItem field="username">
        <Input
          v-model="formData.username"
          placeholder="请输入用户名"
          :prefix-icon="IconUser"
          size="large"
        />
      </FormItem>
      <FormItem field="password">
        <Input
          v-model="formData.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="IconLock"
          size="large"
          @press-enter="handleOk"
        />
      </FormItem>
      <div style="margin-top: 24px">
        <Button type="primary" long size="large" :loading="loading" @click="handleOk">
          登录
        </Button>
        <div style="text-align: center; margin-top: 16px">
          <span style="color: #4e5969">还没有账号？</span>
          <a style="color: #165dff; cursor: pointer" @click="handleRegister">
            立即注册
          </a>
        </div>
      </div>
    </Form>
  </Modal>
</template>

<style scoped>
</style>
