<script setup lang="ts">
import { ref, onMounted, reactive, nextTick } from 'vue'
import {
  Table, Button, Input, Space, Modal, Form, Message, Tag,
  Popconfirm, Pagination, Card, TableColumn, FormItem,
  Row, Col, Textarea, TypographyTitle
} from '@arco-design/web-vue'
import {
  IconSearch, IconEdit, IconDelete, IconPlus, IconRefresh
} from '@arco-design/web-vue/es/icon'
import { addLanguage, updateLanguage, deleteLanguage, listAllLanguages } from '@/api/language'
import type { LanguageInfo, LanguageAddRequest, LanguageUpdateRequest } from '@/types/language'
import { formatDate as formatDateTime } from '@/utils/format'

// --- 状态定义 ---
const loading = ref(false)
const formLoading = ref(false)
const tableData = ref<LanguageInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 弹窗控制
const modalVisible = ref(false)
const isEditMode = ref(false)
const formRef = ref() // 用于表单校验的 ref

// 业务表单模型
const formModel = reactive<LanguageUpdateRequest & LanguageAddRequest>({
  id: 0,
  name: '',
  monacoName: '',
  codeTemplate: '',
  description: ''
})

// --- 核心方法 ---

// 获取列表数据 (整合搜索和分页)
const fetchLanguageList = async () => {
  loading.value = true
  try {
    // 注意：这里的搜索应该传给后端，实现服务端过滤
    const res = await listAllLanguages(pageNum.value, pageSize.value)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      Message.error(res.message || '获取语言列表失败')
    }
  } catch (error) {
    Message.error('网络错误，获取列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索与重置
const handleSearch = () => {
  pageNum.value = 1
  fetchLanguageList()
}

const handleReset = () => {
  searchForm.name = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEditMode.value = false
  // 重置表单数据
  Object.assign(formModel, {
    id: 0,
    name: '',
    monacoName: '',
    codeTemplate: '',
    description: ''
  })
  modalVisible.value = true
}

// 编辑
const handleEdit = (record: LanguageInfo) => {
  isEditMode.value = true
  // 使用 nextTick 确保 Modal 渲染后赋值
  nextTick(() => {
    Object.assign(formModel, {
      id: record.id,
      name: record.name,
      monacoName: record.monacoName,
      codeTemplate: record.codeTemplate,
      description: record.description
    })
  })
  modalVisible.value = true
}

// 删除
const handleDelete = async (record: LanguageInfo) => {
  if (!record.id) return
  try {
    const res = await deleteLanguage(record.id)
    if (res.code === 200) {
      Message.success('删除成功')
      fetchLanguageList()
    }
  } catch (error) {
    Message.error('删除操作失败')
  }
}

// 弹窗确认（提交表单）
const handleModalOk = async () => {
  // 1. 表单校验
  const errors = await formRef.value?.validate()
  if (errors) return false // 返回 false 阻止 Modal 关闭

  formLoading.value = true
  try {
    const apiCall = isEditMode.value ? updateLanguage : addLanguage
    const res = await apiCall(formModel as any)

    if (res.code === 200) {
      Message.success(`${isEditMode.value ? '更新' : '新增'}成功`)
      modalVisible.value = false
      fetchLanguageList()
    } else {
      Message.error(res.message || '操作失败')
    }
  } catch (error) {
    Message.error('系统异常')
  } finally {
    formLoading.value = false
  }
}

// 分页切换
const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchLanguageList()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  pageNum.value = 1
  fetchLanguageList()
}

onMounted(() => {
  fetchLanguageList()
})
</script>

<template>
  <div class="language-manage-page">
    <Card class="search-card" :bordered="false">
      <Form :model="searchForm" layout="inline">
        <Row :gutter="16" style="width: 100%">
          <Col :span="8">
            <FormItem label="语言名称">
              <Input v-model="searchForm.name" placeholder="搜索语言名称..." allow-clear @press-enter="handleSearch" />
            </FormItem>
          </Col>
          <Col :span="16" class="action-buttons">
            <Space>
              <Button type="primary" @click="handleSearch">
                <template #icon>
                  <IconSearch />
                </template>搜索
              </Button>
              <Button @click="handleReset">
                <template #icon>
                  <IconRefresh />
                </template>重置
              </Button>
              <Button type="primary" status="success" @click="handleAdd">
                <template #icon>
                  <IconPlus />
                </template>新增语言
              </Button>
            </Space>
          </Col>
        </Row>
      </Form>
    </Card>

    <Card class="table-card" :bordered="false">
      <Table :loading="loading" :data="tableData" :pagination="false" stripe row-key="id" :bordered="{ cell: true }">
        <template #columns>
          <TableColumn title="ID" data-index="id" :width="70" />
          <TableColumn title="语言名称" :width="150">
            <template #cell="{ record }">
              <Tag color="arcoblue" bordered>{{ record.name }}</Tag>
            </template>
          </TableColumn>
          <TableColumn title="Monaco 名称" :width="150">
            <template #cell="{ record }">
              <Tag color="purple" bordered>{{ record.monacoName }}</Tag>
            </template>
          </TableColumn>
          <TableColumn title="描述" data-index="description" :ellipsis="true" :tooltip="true" />
          <TableColumn title="代码模板" :width="100">
            <template #cell="{ record }">
              <Tag v-if="record.codeTemplate" color="blue" size="small">有模板</Tag>
              <Tag v-else color="gray" size="small">无模板</Tag>
            </template>
          </TableColumn>
          <TableColumn title="最后修改" :width="180">
            <template #cell="{ record }">
              <span class="time-text">{{ formatDateTime(record.gmtModified) }}</span>
            </template>
          </TableColumn>
          <TableColumn title="操作" :width="200" align="center" fixed="right">
            <template #cell="{ record }">
              <Space>
                <Button type="text" size="small" @click="handleEdit(record)">
                  <template #icon>
                    <IconEdit />
                  </template>编辑
                </Button>
                <Popconfirm content="确定要删除该语言吗？" type="warning" @ok="handleDelete(record)">
                  <Button type="text" size="small" status="danger">
                    <template #icon>
                      <IconDelete />
                    </template>删除
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </TableColumn>
        </template>
      </Table>

      <div class="pagination-wrapper">
        <Pagination :total="total" :current="pageNum" :page-size="pageSize" show-total show-page-size
          @change="handlePageChange" @page-size-change="handlePageSizeChange" />
      </div>
    </Card>

    <Modal v-model:visible="modalVisible" :title="isEditMode ? '编辑语言配置' : '新增编程语言'" :ok-loading="formLoading"
      width="640px" unmount-on-close @ok="handleModalOk">
      <Form ref="formRef" :model="formModel" layout="vertical">
        <FormItem field="name" label="语言名称"
          :rules="[{ required: true, message: '请填写语言名称' }, { min: 1, max: 20, message: '长度在 1-20 之间' }]">
          <Input v-model="formModel.name" placeholder="例如：C++" />
        </FormItem>

        <FormItem field="monacoName" label="Monaco 语言名称" :rules="[{ required: true, message: '请填写 Monaco 语言名称' }]">
          <Input v-model="formModel.monacoName" placeholder="例如：java, cpp, python" />
          <template #extra>
            <span style="color: #86909c; font-size: 12px;">
              若不确定对应名称？
              <a :href="'https://microsoft.github.io/monaco-editor/docs.html#modules/languages_definitions_cpp_register.html'"
                target="_blank" style="color: #165dff; cursor: pointer; text-decoration: none;">
                查看官网信息
              </a>
            </span>
          </template>
        </FormItem>

        <FormItem field="description" label="语言描述">
          <Textarea v-model="formModel.description" placeholder="简述该语言版本或特性..."
            :auto-size="{ minRows: 2, maxRows: 4 }" />
        </FormItem>

        <FormItem field="codeTemplate" label="初始代码模板">
          <template #extra>
            <div>该模板将在用户选择此语言时默认填入编辑器</div>
          </template>
          <Textarea v-model="formModel.codeTemplate" placeholder="请输入标准 Hello World 或初始结构..."
            :auto-size="{ minRows: 8, maxRows: 12 }"
            style="font-family: 'JetBrains Mono', monospace; background-color: #f8f9fa;" />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<style scoped>
.language-manage-page {
  padding: 20px;
  background-color: #f4f7f9;
  min-height: calc(100vh - 60px);
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
}

.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.time-text {
  color: #86909c;
  font-size: 12px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.arco-table-th) {
  background-color: #f7f8fa !important;
}

/* 程序员专属字体 */
:deep(.arco-textarea) {
  font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
}
</style>
