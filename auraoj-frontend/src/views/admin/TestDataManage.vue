<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Card,
  Button,
  Table,
  TableColumn,
  Space,
  Input,
  Modal,
  Form,
  FormItem,
  Message,
  Upload,
  Tooltip
} from '@arco-design/web-vue'
import { IconLeft, IconUpload, IconPlus, IconEdit, IconDelete } from '@arco-design/web-vue/es/icon'
import { getTestDataList, deleteTestData, renameTestData, createTestData } from '@/api/testData'
import type { ProblemCaseFileResponse } from '@/types/testData'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const problemId = ref<number>(0)
const testDataCase = ref<ProblemCaseFileResponse[]>([])

// 展示用的列表（将每个用例拆分成输入和输出两个文件）
const testDataList = ref<any[]>([])

interface TestDataFileItem {
  caseId: number
  fileName: string
  fileSize: number
  fileType: 'input' | 'output'
  gmtModified: string
}

// 重命名弹窗相关
const renameModalVisible = ref(false)
const renamingFile = ref<TestDataItem>({})
const newFileName = ref('')

// 创建测试数据弹窗相关
const createModalVisible = ref(false)
const createFileName = ref('')
const createInputContent = ref('')
const createOutputContent = ref('')

interface TestDataItem {
  id: number
  fileName: string
  fileSize: number
  gmtModified: string
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const fetchTestDataList = async () => {
  if (!problemId.value) return

  loading.value = true
  try {
    const res = await getTestDataList(problemId.value)
    if (res.code === 200) {
      testDataCase.value = res.data || []
      // 将每个用例拆分成输入和输出两个文件
      const fileList: TestDataFileItem[] = []
      testDataCase.value.forEach((item) => {
        if (item.inputFile) {
          fileList.push({
            caseId: item.id || 0,
            fileName: item.inputFile,
            fileSize: item.inputFileSize || 0,
            fileType: 'input',
            gmtModified: item.gmtModified || ''
          })
        }
        if (item.outputFile) {
          fileList.push({
            caseId: item.id || 0,
            fileName: item.outputFile,
            fileSize: item.outputFileSize || 0,
            fileType: 'output',
            gmtModified: item.gmtModified || ''
          })
        }
      })
      testDataList.value = fileList
    } else {
      Message.error(res.message || '获取测试数据列表失败')
    }
  } catch (error) {
    console.error('获取测试数据列表失败:', error)
    Message.error('获取测试数据列表失败')
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.push('/admin/problem')
}

const handleOpenRenameModal = (file: TestDataFileItem) => {
  renamingFile.value = {
    id: file.caseId,
    fileName: file.fileName,
    fileSize: file.fileSize,
    gmtModified: file.gmtModified
  }
  newFileName.value = file.fileName
  renameModalVisible.value = true
}

const handleRename = async () => {
  if (!renamingFile.value || !newFileName.value.trim()) {
    Message.warning('请输入新文件名')
    return
  }

  try {
    const res = await renameTestData(renamingFile.value.caseId, newFileName.value.trim())
    if (res.code === 200) {
      Message.success('重命名成功')
      renameModalVisible.value = false
      fetchTestDataList()
    } else {
      Message.error(res.message || '重命名失败')
    }
  } catch (error) {
    console.error('重命名失败:', error)
    Message.error('重命名失败')
  }
}

const handleDelete = async (file: TestDataFileItem) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除测试用例 "${file.fileName}" 吗？此操作不可恢复。`,
    okText: '确认',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await deleteTestData(file.caseId)
        if (res.code === 200) {
          Message.success('删除成功')
          fetchTestDataList()
        } else {
          Message.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        Message.error('删除失败')
      }
    }
  })
}

const handleOpenCreateModal = () => {
  createFileName.value = ''
  createInputContent.value = ''
  createOutputContent.value = ''
  createModalVisible.value = true
}

const handleCreate = async () => {
  if (!createFileName.value.trim()) {
    Message.warning('请输入文件名')
    return
  }

  try {
    const res = await createTestData({
      problemId: problemId.value,
      fileName: createFileName.value.trim(),
      inputContent: createInputContent.value,
      outputContent: createOutputContent.value
    })
    if (res.code === 200) {
      Message.success('创建成功')
      createModalVisible.value = false
      fetchTestDataList()
    } else {
      Message.error(res.message || '创建失败')
    }
  } catch (error) {
    console.error('创建失败:', error)
    Message.error('创建失败')
  }
}

const handleUploadSuccess = () => {
  Message.success('上传成功')
  fetchTestDataList()
}

const handleUploadError = () => {
  Message.error('上传失败')
}

onMounted(() => {
  const id = route.params.problemId
  if (id) {
    problemId.value = Number(id)
    fetchTestDataList()
  } else {
    Message.error('题目ID不存在')
    router.push('/admin/problem')
  }
})
</script>

<template>
  <div class="testdata-manage-page">
    <div class="page-header">
      <Button type="text" class="back-btn" @click="handleBack">
        <template #icon>
          <IconLeft />
        </template>
        返回
      </Button>
      <span class="page-path">题目管理 / 管理测试数据</span>
    </div>

    <div class="content-wrapper">
      <Card class="table-card" :bordered="false">
        <div class="action-bar">
          <Space>
            <Upload action="/api/testdata/upload" :data="{ problemId: problemId }" :show-file-list="false"
              accept=".in,.out,.txt" @success="handleUploadSuccess" @error="handleUploadError">
              <Button type="primary">
                <template #icon>
                  <IconUpload />
                </template>
                上传文件
              </Button>
            </Upload>
            <Button @click="handleOpenCreateModal">
              <template #icon>
                <IconPlus />
              </template>
              创建测试数据
            </Button>
          </Space>
        </div>

        <Table :loading="loading" :data="testDataList" :pagination="false" stripe :bordered="false"
          class="testdata-table">
          <template #columns>
            <TableColumn title="文件类型" :width="100">
              <template #cell="{ record }">
                <a-tag :color="record.fileType === 'input' ? 'blue' : 'green'">
                  {{ record.fileType === 'input' ? '输入' : '输出' }}
                </a-tag>
              </template>
            </TableColumn>
            <TableColumn title="文件名" data-index="fileName">
              <template #cell="{ record }">
                <span class="file-name">{{ record.fileName }}</span>
              </template>
            </TableColumn>
            <TableColumn title="文件大小" :width="120">
              <template #cell="{ record }">
                {{ formatFileSize(record.fileSize) }}
              </template>
            </TableColumn>
            <TableColumn title="修改时间" :width="180">
              <template #cell="{ record }">
                {{ record.gmtModified }}
              </template>
            </TableColumn>
            <TableColumn title="操作" align="center">
              <template #cell="{ record }">
                <Space size="small">
                  <Tooltip content="删除">
                    <Button type="text" status="danger" size="small" class="action-btn" @click="handleDelete(record)">
                      <template #icon>
                        <IconDelete />
                      </template>
                    </Button>
                  </Tooltip>
                </Space>
              </template>
            </TableColumn>
          </template>
        </Table>

        <div v-if="testDataList.length === 0 && !loading" class="empty-state">
          <div class="empty-text">暂无测试数据，请上传或创建</div>
        </div>
      </Card>
    </div>

    <!-- 重命名弹窗 -->
    <Modal v-model:visible="renameModalVisible" title="重命名文件" :mask-closable="false" @confirm="handleRename">
      <Form layout="vertical">
        <FormItem label="新文件名">
          <Input v-model="newFileName" placeholder="请输入新文件名" />
        </FormItem>
      </Form>
    </Modal>

    <!-- 创建测试数据弹窗 -->
    <Modal v-model:visible="createModalVisible" title="创建测试数据" :mask-closable="false" @confirm="handleCreate">
      <Form layout="vertical">
        <FormItem label="文件名">
          <Input v-model="createFileName" placeholder="例如：1" />
        </FormItem>
        <FormItem label="输入内容">
          <Textarea v-model="createInputContent" placeholder="请输入真实的测试样例输入" :auto-size="{ minRows: 6, maxRows: 12 }"
            class="monospace-input" />
        </FormItem>
        <FormItem label="输出内容">
          <Textarea v-model="createOutputContent" placeholder="请输入真实的测试样例输出" :auto-size="{ minRows: 6, maxRows: 12 }"
            class="monospace-input" />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<style scoped>
.testdata-manage-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f2f3f5;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid var(--color-border-2);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #4e5969;
  font-size: 15px;
}

.page-path {
  font-size: 16px;
  font-weight: 500;
  color: #1d2129;
}

.content-wrapper {
  flex: 1;
  padding: 24px;
}

.table-card {
  border-radius: 8px;
}

.action-bar {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e6eb;
}

.testdata-table {
  margin-top: 0;
}

.file-name {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
  font-size: 15px;
  color: #1d2129;
}

.action-btn {
  padding: 4px 8px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.empty-text {
  color: #86909c;
  font-size: 14px;
}

/* 等宽字体，用于样例输入输出的对齐 */
.monospace-input :deep(textarea) {
  font-family: 'JetBrains Mono', 'Consolas', 'Fira Code', monospace;
  background-color: #fafafa;
  border: 1px solid var(--color-border-2);
}
</style>
