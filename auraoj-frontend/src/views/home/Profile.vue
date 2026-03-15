<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '@/store/user'
import { Card, Avatar, Tag, Row, Col, Divider, Message, Modal, Button } from '@arco-design/web-vue'
import { IconCamera, IconUser, IconEmail, IconPhone, IconLocation, IconInfo, IconUpload } from '@arco-design/web-vue/es/icon'
import { uploadAvatar, getUserInfo } from '@/api/user'

const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const getGenderText = (gender?: number) => {
  switch (gender) {
    case 0: return '女'
    case 1: return '男'
    default: return '未知'
  }
}

const getRoleText = (role?: string) => {
  switch (role) {
    case 'admin': return '管理员'
    case 'teacher': return '老师'
    default: return '学生'
  }
}

const getRoleTagColor = (role?: string) => {
  switch (role) {
    case 'admin': return 'red'
    case 'teacher': return 'orange'
    default: return 'arcoblue'
  }
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 头像上传相关
const avatarModalVisible = ref(false)
const previewUrl = ref('')
const selectedFile = ref<File | null>(null)
const uploading = ref(false)
const fileInputRef = ref<HTMLInputElement>()

const handleChangeAvatar = () => {
  previewUrl.value = userInfo.value?.avatar || ''
  selectedFile.value = null
  avatarModalVisible.value = true
}

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) return

  if (!file.type.startsWith('image/')) {
    Message.error('请选择图片文件')
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    Message.error('图片大小不能超过 5MB')
    return
  }

  selectedFile.value = file

  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

const handleUploadAvatar = async () => {
  if (!selectedFile.value) {
    Message.warning('请选择图片')
    return
  }

  if (!userInfo.value?.id) {
    Message.error('用户信息异常')
    return
  }

  uploading.value = true

  try {
    const res = await uploadAvatar(userInfo.value.id, selectedFile.value)

    if (res.code === 200) {
      await refreshUserInfo()
      Message.success('头像更新成功')
      avatarModalVisible.value = false
      selectedFile.value = null
      previewUrl.value = ''
    } else {
      Message.error(res.message || '上传失败')
    }
  } catch (error: any) {
    console.error('上传失败:', error)
    Message.error(error.response?.data?.message || '上传失败，请稍后重试')
  } finally {
    uploading.value = false
  }
}

const refreshUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200 && res.data) {
      userStore.setUserInfo(res.data)
    }
  } catch (error) {
    console.error('刷新用户信息失败:', error)
  }
}

const handleCancel = () => {
  avatarModalVisible.value = false
  selectedFile.value = null
  previewUrl.value = ''
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

const triggerFileInput = () => {
  fileInputRef.value?.click()
}
</script>

<template>
  <div class="profile-page">
    <Row :gutter="24">
      <Col :xs="24" :lg="8">
        <Card class="profile-card user-card">
          <div class="user-header">
            <div class="avatar-wrapper">
              <Avatar :size="180" :style="{ backgroundColor: '#165DFF', fontSize: '72px' }">
                <template v-if="userInfo?.avatar">
                  <img :src="userInfo.avatar" />
                </template>
                <template v-else>
                  {{ userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
                </template>
              </Avatar>
              <div class="avatar-overlay" @click="handleChangeAvatar">
                <IconCamera class="camera-icon" />
                <span>修改头像</span>
              </div>
            </div>
            <div class="user-name">{{ userInfo?.username || '用户' }}</div>
            <Tag :color="getRoleTagColor(userInfo?.role)" size="large">
              {{ getRoleText(userInfo?.role) }}
            </Tag>
          </div>

          <Divider />

          <div class="info-list">
            <div class="info-item">
              <div class="info-label">
                <IconUser class="info-icon" />
                <span>性别</span>
              </div>
              <div class="info-value">{{ getGenderText(userInfo?.gender) }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <IconEmail class="info-icon" />
                <span>邮箱</span>
              </div>
              <div class="info-value">{{ userInfo?.email || '-' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <IconPhone class="info-icon" />
                <span>手机号</span>
              </div>
              <div class="info-value">{{ userInfo?.phone || '-' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <IconLocation class="info-icon" />
                <span>学校/机构</span>
              </div>
              <div class="info-value">{{ userInfo?.school || '-' }}</div>
            </div>
          </div>
        </Card>
      </Col>

      <Col :xs="24" :lg="16">
        <Card class="profile-card">
          <template #title>
            <div class="card-title">
              <IconInfo class="title-icon" />
              <span>个人资料</span>
            </div>
          </template>

          <div class="detail-list">
            <div class="detail-item">
              <div class="detail-label">用户 ID</div>
              <div class="detail-value">{{ userInfo?.id || '-' }}</div>
            </div>
            <div class="detail-item">
              <div class="detail-label">用户名</div>
              <div class="detail-value">{{ userInfo?.username || '-' }}</div>
            </div>
            <div class="detail-item">
              <div class="detail-label">个性签名</div>
              <div class="detail-value">{{ userInfo?.signature || '暂无签名' }}</div>
            </div>
            <div class="detail-item">
              <div class="detail-label">等级</div>
              <div class="detail-value">
                <Tag color="blue">Lv.{{ userInfo?.level || 1 }}</Tag>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-label">积分</div>
              <div class="detail-value">
                <span class="score">{{ userInfo?.score || 0 }}</span>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-label">账号状态</div>
              <div class="detail-value">
                <Tag :color="userInfo?.status === 1 ? 'red' : 'green'" size="small">
                  {{ userInfo?.status === 1 ? '已禁用' : '正常' }}
                </Tag>
              </div>
            </div>
            <div class="detail-item">
              <div class="detail-label">注册时间</div>
              <div class="detail-value">{{ formatDate(userInfo?.loginTime) }}</div>
            </div>
          </div>
        </Card>

        <Card class="profile-card stats-card">
          <template #title>
            <div class="card-title">
              <span>数据统计</span>
            </div>
          </template>
          <div class="stats-placeholder">
            <span>数据统计功能开发中...</span>
          </div>
        </Card>
      </Col>
    </Row>

    <Modal v-model:visible="avatarModalVisible" title="修改头像" :footer="false" width="400" :closable="false"
      :mask-closable="false">
      <div class="avatar-upload-modal">
        <div class="preview-area">
          <Avatar :size="160" :style="{ backgroundColor: '#165DFF', fontSize: '64px' }">
            <template v-if="previewUrl">
              <img :src="previewUrl" />
            </template>
            <template v-else>
              {{ userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
            </template>
          </Avatar>
        </div>

        <input ref="fileInputRef" type="file" accept="image/*" style="display: none" @change="handleFileChange" />

        <div class="upload-actions">
          <Button long @click="triggerFileInput" :disabled="uploading">
            <template #icon>
              <IconUpload />
            </template>
            选择图片
          </Button>
          <div class="upload-tips">
            <span>支持 JPG、PNG 格式，大小不超过 5MB</span>
          </div>
        </div>

        <div class="modal-footer">
          <Button @click="handleCancel" :disabled="uploading">取消</Button>
          <Button type="primary" @click="handleUploadAvatar" :loading="uploading" :disabled="!selectedFile">
            保存
          </Button>
        </div>
      </div>
    </Modal>
  </div>
</template>

<style scoped>
.profile-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.user-card .user-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  margin-bottom: 16px;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.camera-icon {
  font-size: 32px;
  margin-bottom: 4px;
}

.user-name {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 8px;
}

.info-list {
  padding: 0 16px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #e5e6e8;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #86909c;
  font-size: 14px;
}

.info-icon {
  font-size: 16px;
}

.info-value {
  color: #1d2129;
  font-size: 14px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.title-icon {
  font-size: 18px;
}

.detail-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
}

.detail-label {
  font-size: 13px;
  color: #86909c;
}

.detail-value {
  font-size: 15px;
  color: #1d2129;
  font-weight: 500;
}

.score {
  color: #ff7d00;
  font-weight: 600;
}

.stats-placeholder {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c9cdd4;
  font-size: 14px;
  background: #f7f8fa;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .detail-list {
    grid-template-columns: 1fr;
  }
}

.avatar-upload-modal {
  padding: 20px 0;
}

.preview-area {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.upload-actions {
  margin-bottom: 16px;
}

.upload-tips {
  text-align: center;
  margin-top: 8px;
  font-size: 12px;
  color: #86909c;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}
</style>
