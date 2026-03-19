<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue'
import {
  Table,
  Button,
  Input,
  Space,
  Modal,
  Form,
  Select,
  Message,
  Tag,
  Popconfirm,
  Pagination,
  Avatar,
  Card,
  TableColumn,
  FormItem,
  Option,
  Row,
  Col,
  Divider
} from '@arco-design/web-vue'
import {
  IconSearch,
  IconEdit,
  IconDelete,
  IconPlus,
  IconUser,
  IconRefresh,
  IconDownload,
  IconSettings
} from '@arco-design/web-vue/es/icon'
import { getAllUsers, updateCurrentUser, deleteUser } from '@/api/user'
import type { UserInfo, UpdateUserParams } from '@/types/user'

const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userList = ref<UserInfo[]>([])

// 搜索表单模型
const searchForm = reactive({
  username: '',
  phone: '',
  email: '',
  role: '',
  status: '' as number | ''
})

const roleOptions = [
  { label: '全部', value: '' },
  { label: '学生', value: 'student' },
  { label: '老师', value: 'teacher' },
  { label: '管理员', value: 'admin' }
]

const statusOptions = [
  { label: '全部', value: '' },
  { label: '正常', value: 0 },
  { label: '已禁用', value: 1 }
]

const genderOptions = [
  { label: '未知', value: -1 },
  { label: '女', value: 0 },
  { label: '男', value: 1 }
]

const filteredUserList = computed(() => {
  return userList.value.filter((user) => {
    const matchName = !searchForm.username || user.username?.toLowerCase().includes(searchForm.username.toLowerCase())
    const matchEmail = !searchForm.email || user.email?.toLowerCase().includes(searchForm.email.toLowerCase())
    const matchPhone = !searchForm.phone || user.phone?.includes(searchForm.phone)
    const matchRole = !searchForm.role || user.role === searchForm.role
    const matchStatus = searchForm.status === '' || user.status === searchForm.status
    return matchName && matchEmail && matchPhone && matchRole && matchStatus
  })
})

const handleSearch = () => {
  pageNum.value = 1
  fetchUserList()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.phone = ''
  searchForm.email = ''
  searchForm.role = ''
  searchForm.status = ''
  handleSearch()
}

const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await getAllUsers(pageNum.value, pageSize.value)
    if (res.code === 200 && res.data) {
      userList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    Message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchUserList()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  pageNum.value = 1
  fetchUserList()
}

const editModalVisible = ref(false)
const editingUser = ref<UserInfo | null>(null)
const editFormRef = ref()
const editFormData = ref<UpdateUserParams>({})

const handleAdd = () => {
  Message.info('新建用户功能待开发')
}

const handleEdit = (user: UserInfo) => {
  editingUser.value = user
  editFormData.value = {
    username: user.username,
    phone: user.phone,
    email: user.email,
    avatar: user.avatar || '',
    gender: user.gender,
    school: user.school || '',
    signature: user.signature || ''
  }
  editModalVisible.value = true
}

const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate()
    if (!editingUser.value?.id) return

    // 1. 找出真正被修改过的字段 (Diff)
    const changedData: Partial<UpdateUserParams> = {}

    // 遍历表单数据，和原始数据进行对比
    for (const key in editFormData.value) {
      const formValue = editFormData.value[key as keyof UpdateUserParams]
      const originalValue = editingUser.value[key as keyof UserInfo]

      // 如果值不一样，说明用户修改了这个字段
      if (formValue !== originalValue) {
        // @ts-ignore
        changedData[key] = formValue
      }
    }

    // 2. 如果什么都没改，直接关闭弹窗，不发请求
    if (Object.keys(changedData).length === 0) {
      Message.info('未做任何修改')
      editModalVisible.value = false
      return
    }

    // 3. 只把 changedData 传给后端
    const res = await updateCurrentUser(editingUser.value.id, changedData)
    if (res.code === 200) {
      Message.success('修改成功')
      editModalVisible.value = false
      fetchUserList()
    } else {
      Message.error(res.message || '修改失败')
    }
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (userId: number) => {
  try {
    const res = await deleteUser(userId)
    if (res.code === 200) {
      Message.success('删除成功')
      fetchUserList()
    } else {
      Message.error(res.message || '删除失败')
    }
  } catch (error) {
    Message.error('删除失败')
  }
}

const getGenderText = (gender?: number) => {
  switch (gender) {
    case 0: return '女'
    case 1: return '男'
    default: return '未知'
  }
}

const getRoleTagColor = (role?: string) => {
  switch (role) {
    case 'admin': return 'red'
    case 'teacher': return 'orange'
    default: return 'arcoblue'
  }
}

const getRoleText = (role?: string) => {
  switch (role) {
    case 'admin': return '管理员'
    case 'teacher': return '老师'
    default: return '学生'
  }
}

const getStatusTagColor = (status?: number) => {
  return status === 1 ? 'red' : 'green'
}

const getStatusText = (status?: number) => {
  return status === 1 ? '已禁用' : '正常'
}

const formatDateTime = (dateStr?: string) => {
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

onMounted(() => {
  fetchUserList()
})
</script>

<template>
  <div class="user-manage">
    <Card class="general-card" title="用户信息" :bordered="false">
      <Row>
        <Col :flex="1">
          <Form :model="searchForm" :label-col-props="{ span: 6 }" :wrapper-col-props="{ span: 18 }" label-align="left">
            <Row :gutter="16">
              <Col :span="8">
                <FormItem field="username" label="用户名">
                  <Input v-model="searchForm.username" placeholder="请输入用户名" allow-clear />
                </FormItem>
              </Col>
              <Col :span="8">
                <FormItem field="email" label="邮箱">
                  <Input v-model="searchForm.email" placeholder="请输入邮箱" allow-clear />
                </FormItem>
              </Col>
              <Col :span="8">
                <FormItem field="role" label="角色">
                  <Select v-model="searchForm.role" :options="roleOptions" placeholder="全部" allow-clear />
                </FormItem>
              </Col>
              <Col :span="8">
                <FormItem field="phone" label="手机号">
                  <Input v-model="searchForm.phone" placeholder="请输入手机号" allow-clear />
                </FormItem>
              </Col>
              <Col :span="8">
                <FormItem field="status" label="状态">
                  <Select v-model="searchForm.status" :options="statusOptions" placeholder="全部" allow-clear />
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Col>
        <Divider style="height: 84px" direction="vertical" />
        <Col :flex="'86px'" style="text-align: right">
          <Space direction="vertical" :size="18">
            <Button type="primary" @click="handleSearch">
              <template #icon>
                <IconSearch />
              </template>
              查询
            </Button>
            <Button @click="handleReset">
              <template #icon>
                <IconRefresh />
              </template>
              重置
            </Button>
          </Space>
        </Col>
      </Row>

      <Divider style="margin-top: 0" />

      <Row style="margin-bottom: 16px">
        <Col :span="12">
          <Space>
            <Button type="primary" @click="handleAdd">
              <template #icon>
                <IconPlus />
              </template>
              添加
            </Button>
            <Button>批量导入</Button>
          </Space>
        </Col>
        <Col :span="12" style="display: flex; align-items: center; justify-content: flex-end">
          <Space>
            <!-- <Button type="primary" status="success" size="small">
              <template #icon>
                <IconDownload />
              </template>
              下载
            </Button> -->
            <Button type="text" @click="fetchUserList">
              <template #icon>
                <IconRefresh size="18" style="color: #4e5969;" />
              </template>
            </Button>
            <Button type="text">
              <template #icon>
                <IconSettings size="18" style="color: #4e5969;" />
              </template>
            </Button>
          </Space>
        </Col>
      </Row>

      <Table :loading="loading" :data="filteredUserList" :pagination="false" stripe :scroll="{ x: 1200 }"
        :bordered="false">
        <template #columns>
          <TableColumn title="#" :width="60">
            <template #cell="{ rowIndex }">
              {{ rowIndex + 1 }}
            </template>
          </TableColumn>
          <TableColumn title="用户信息" :width="200">
            <template #cell="{ record }">
              <div class="user-info-cell">
                <Avatar :size="32" :style="{ backgroundColor: '#165DFF' }">
                  <template v-if="record.avatar">
                    <img :src="record.avatar" />
                  </template>
                  <template v-else>
                    <IconUser />
                  </template>
                </Avatar>
                <div class="user-detail">
                  <div class="username">{{ record.username }}</div>
                  <div class="user-id">ID: {{ record.id }}</div>
                </div>
              </div>
            </template>
          </TableColumn>
          <TableColumn title="性别" :width="80">
            <template #cell="{ record }">
              {{ getGenderText(record.gender) }}
            </template>
          </TableColumn>
          <TableColumn title="手机号" :width="130">
            <template #cell="{ record }">
              {{ record.phone || '-' }}
            </template>
          </TableColumn>
          <TableColumn title="邮箱" :width="200">
            <template #cell="{ record }">
              {{ record.email || '-' }}
            </template>
          </TableColumn>
          <TableColumn title="学校" :width="150">
            <template #cell="{ record }">
              {{ record.school || '-' }}
            </template>
          </TableColumn>
          <TableColumn title="角色" :width="100">
            <template #cell="{ record }">
              <Tag :color="getRoleTagColor(record.role)" size="medium">
                {{ getRoleText(record.role) }}
              </Tag>
            </template>
          </TableColumn>
          <TableColumn title="等级" :width="80">
            <template #cell="{ record }">
              {{ record.level ?? '-' }}
            </template>
          </TableColumn>
          <TableColumn title="积分" :width="80">
            <template #cell="{ record }">
              {{ record.score ?? '-' }}
            </template>
          </TableColumn>
          <TableColumn title="状态" :width="80">
            <template #cell="{ record }">
              <Tag :color="getStatusTagColor(record.status)" size="medium">
                {{ getStatusText(record.status) }}
              </Tag>
            </template>
          </TableColumn>
          <TableColumn title="创建时间" :width="180">
            <template #cell="{ record }">
              {{ formatDateTime(record.gmtCreate) }}
            </template>
          </TableColumn>
          <TableColumn title="操作" :width="140" align="center" fixed="right">
            <template #cell="{ record }">
              <Space size="small">
                <Button type="text" size="small" @click="handleEdit(record)">
                  编辑
                </Button>
                <Popconfirm position="tr" content="确定要删除吗？" ok-text="确定" cancel-text="取消"
                  @ok="handleDelete(record.id!)">
                  <Button type="text" status="danger" size="small">
                    删除
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </TableColumn>
        </template>
      </Table>
      <div class="pagination-wrapper">
        <Pagination :total="total" :current="pageNum" :page-size="pageSize" :show-total="true" :show-page-size="true"
          :page-size-options="[10, 20, 50, 100]" @change="handlePageChange" @page-size-change="handlePageSizeChange" />
      </div>
    </Card>

    <Modal v-model:visible="editModalVisible" title="编辑用户" @ok="handleEditSubmit" :unmount-on-close="true" width="600">
      <Form ref="editFormRef" :model="editFormData" layout="vertical">
        <FormItem field="username" label="用户名">
          <Input v-model="editFormData.username" placeholder="请输入用户名" />
        </FormItem>
        <FormItem field="phone" label="手机号">
          <Input v-model="editFormData.phone" placeholder="请输入手机号" />
        </FormItem>
        <FormItem field="email" label="邮箱">
          <Input v-model="editFormData.email" placeholder="请输入邮箱" />
        </FormItem>
        <FormItem field="gender" label="性别">
          <Select v-model="editFormData.gender" placeholder="请选择性别">
            <Option v-for="option in genderOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </Option>
          </Select>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<style scoped>
.user-manage {
  padding: 0;
}

.general-card {
  border-radius: 4px;
}

:deep(.arco-form-item-layout-inline) {
  margin-right: 0;
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #1d2129;
}

.user-id {
  font-size: 12px;
  color: #86909c;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
