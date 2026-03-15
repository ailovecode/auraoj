<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {
  Table,
  Button,
  Input,
  Space,
  Form,
  Select,
  Message,
  Tag,
  Popconfirm,
  Pagination,
  Card,
  TableColumn,
  FormItem,
  Row,
  Col,
  Divider
} from '@arco-design/web-vue'
import {
  IconSearch,
  IconPlus,
  IconRefresh,
  IconDownload,
  IconSettings,
} from '@arco-design/web-vue/es/icon'
import { queryAllProblems } from '@/api/problem'
import type { AdminProblemInfo } from '@/types/problem'

const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const problemList = ref<AdminProblemInfo[]>([])

// 搜索表单模型
const searchForm = ref({
  title: '',
  difficulty: '' as number | '',
  status: '' as number | ''
})

const difficultyOptions = [
  { label: '全部', value: '' },
  { label: '简单', value: 1 },
  { label: '中等', value: 2 },
  { label: '困难', value: 3 }
]

const statusOptions = [
  { label: '全部', value: '' },
  { label: '公开', value: 1 },
  { label: '私有', value: 2 },
  { label: '比赛中', value: 3 }
]

const filteredProblemList = computed(() => {
  return problemList.value.filter((problem) => {
    const matchTitle = !searchForm.value.title || problem.title.toLowerCase().includes(searchForm.value.title.toLowerCase())
    const matchDifficulty = searchForm.value.difficulty === '' || problem.difficulty === Number(searchForm.value.difficulty)
    const matchStatus = searchForm.value.status === '' || Number(problem.status) === Number(searchForm.value.status)
    return matchTitle && matchDifficulty && matchStatus
  })
})

const handleSearch = () => {
  pageNum.value = 1
  fetchProblemList()
}

const handleReset = () => {
  searchForm.value.title = ''
  searchForm.value.difficulty = ''
  searchForm.value.status = ''
  handleSearch()
}

const fetchProblemList = async () => {
  loading.value = true
  try {
    const res = await queryAllProblems(pageNum.value, pageSize.value, '', '')
    if (res.code === 200 && res.data) {
      problemList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取题目列表失败:', error)
    Message.error('获取题目列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchProblemList()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  pageNum.value = 1
  fetchProblemList()
}

const handleAdd = () => {
  Message.info('新建题目功能待开发')
}

const handleEdit = (problem: AdminProblemInfo) => {
  Message.info(`编辑题目：${problem.title}`)
}

const handleDelete = async (problemId: number) => {
  Message.info(`删除题目：${problemId}`)
}

const handleManageTestData = (problem: AdminProblemInfo) => {
  Message.info(`管理测试数据：${problem.title}`)
}

const getDifficultyText = (difficulty?: string) => {
  switch (difficulty) {
    case 'easy': return '简单'
    case 'medium': return '中等'
    case 'hard': return '困难'
    default: return '未知'
  }
}

const getDifficultyTagColor = (difficulty?: string) => {
  switch (difficulty) {
    case 'easy': return 'green'
    case 'medium': return 'orange'
    case 'hard': return 'red'
    default: return 'gray'
  }
}

const getStatusTagColor = (status?: number) => {
  switch (status) {
    case 1: return 'green'
    case 2: return 'red'
    case 3: return 'orange'
    default: return 'gray'
  }
}

const getStatusText = (status?: number) => {
  switch (status) {
    case 1: return '公开'
    case 2: return '私有'
    case 3: return '比赛中'
    default: return '未知'
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

onMounted(() => {
  fetchProblemList()
})
</script>

<template>
  <div class="problem-manage">
    <Card class="general-card" title="查询表格" :bordered="false">
      <Row>
        <Col :flex="1">
          <Form :model="searchForm" :label-col-props="{ span: 6 }" :wrapper-col-props="{ span: 18 }" label-align="left">
            <Row :gutter="16">
              <Col :span="8">
                <FormItem field="title" label="题目名称">
                  <Input v-model="searchForm.title" placeholder="请输入题目名称" allow-clear />
                </FormItem>
              </Col>
              <Col :span="8">
                <FormItem field="difficulty" label="难度">
                  <Select v-model="searchForm.difficulty" :options="difficultyOptions" placeholder="全部" allow-clear />
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
              新建
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
            <Button type="text" @click="fetchProblemList">
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

      <Table :loading="loading" :data="filteredProblemList" :pagination="false" stripe :scroll="{ x: 1400 }"
        :bordered="false">
        <template #columns>
          <TableColumn title="#" :width="60">
            <template #cell="{ rowIndex }">
              {{ rowIndex + 1 }}
            </template>
          </TableColumn>
          <TableColumn data-index="title" title="题目名称" :width="80" />
          <!-- <TableColumn title="题目信息" :width="250">
            <template #cell="{ record }">
              <div class="problem-info-cell">
                <div class="problem-icon">
                  <IconFile :size="24" />
                </div>
                <div class="problem-detail">
                  <div class="problem-title">{{ record.title }}</div>
                  <div class="problem-id">ID: {{ record.id }}</div>
                </div>
              </div>
            </template>
          </TableColumn> -->
          <TableColumn title="难度" :width="80">
            <template #cell="{ record }">
              <Tag :color="getDifficultyTagColor(record.difficulty)" size="medium">
                {{ getDifficultyText(record.difficulty) }}
              </Tag>
            </template>
          </TableColumn>
          <TableColumn data-index="submitNum" title="提交数" :width="80" />
          <TableColumn data-index="acceptNum" title="通过数" :width="80" />
          <TableColumn title="通过率" :width="80">
            <template #cell="{ record }">
              {{ (record.passRate || 0).toFixed(1) }}%
            </template>
          </TableColumn>
          <TableColumn title="状态" :width="80">
            <template #cell="{ record }">
              <Tag :color="getStatusTagColor(record.status)" size="small">
                {{ getStatusText(record.status) }}
              </Tag>
            </template>
          </TableColumn>
          <TableColumn title="标签" :width="150">
            <template #cell="{ record }">
              <div class="tags-cell">
                <Tag v-for="tag in record.tags" :key="tag.id" size="small" color="arcoblue">
                  {{ tag.name }}
                </Tag>
                <span v-if="!record.tags || record.tags.length === 0" class="no-tags">-</span>
              </div>
            </template>
          </TableColumn>
          <TableColumn data-index="judgeType" title="判题方式" :width="100" />
          <TableColumn title="创建时间" :width="160">
            <template #cell="{ record }">
              {{ formatDate(record.gmtCreate) }}
            </template>
          </TableColumn>
          <TableColumn title="修改时间" :width="160">
            <template #cell="{ record }">
              {{ formatDate(record.gmtModified) }}
            </template>
          </TableColumn>
          <TableColumn title="操作" :width="220" align="center" fixed="right">
            <template #cell="{ record }">
              <Space size="small">
                <Button type="text" size="small" class="compact-btn" @click="handleEdit(record)">
                  编辑
                </Button>
                <Popconfirm position="tr" content="确定要删除吗？" ok-text="确定" cancel-text="取消"
                  @ok="handleDelete(record.id!)">
                  <Button type="text" status="danger" size="small" class="compact-btn">
                    删除
                  </Button>
                </Popconfirm>
                <Button type="text" size="small" class="compact-btn" @click="handleManageTestData(record)">
                  管理测试数据
                </Button>
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
  </div>
</template>

<style scoped>
.problem-manage {
  padding: 0;
}

.compact-btn {
  padding-left: 8px !important;
  padding-right: 8px !important;
}

.general-card {
  border-radius: 4px;
}

.problem-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.problem-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #e8f3ff;
  color: #165dff;
}

.problem-detail {
  display: flex;
  flex-direction: column;
}

.problem-title {
  font-size: 14px;
  font-weight: 500;
  color: #1d2129;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.problem-id {
  font-size: 12px;
  color: #86909c;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.operation-cell {
  display: flex;
  align-items: center;
  gap: 4px;
}

.operation-cell :deep(.arco-btn) {
  padding: 0 4px;
  font-size: 13px;
}

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}

.tags-cell .no-tags {
  color: #c9cdd4;
  font-size: 13px;
}
</style>
