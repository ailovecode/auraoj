<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Card,
  Button,
  Table,
  TableColumn,
  Space,
  Input,
  Select,
  Pagination,
  Tag,
  Message,
  Form,
  FormItem
} from '@arco-design/web-vue'
import { IconRefresh, IconSearch } from '@arco-design/web-vue/es/icon'
import { getSubmissionInfo } from '@/api/judge'
import type { ShowSubmissionRequest, ShowSubmissionInfo } from '@/types/judge'
import { formatDate, getLanguageText, getSubmissionStatusInfo, type SubmissionStatusKey } from '@/utils/format'

const router = useRouter()

const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const submissionList = ref<ShowSubmissionInfo[]>([])

const searchForm = ref({
  problemId: '',
  username: '',
  language: '',
  status: ''
})

const languageOptions = [
  { label: '全部语言', value: '' },
  { label: 'Java', value: 'java' },
  { label: 'C++', value: 'cpp' },
  { label: 'Python', value: 'python' },
  { label: 'JavaScript', value: 'javascript' }
]

const statusOptions = [
  { label: '全部状态', value: '' },
  { label: 'Pending', value: 'Pending' },
  { label: 'Accepted', value: 'Accepted' },
  { label: 'Wrong Answer', value: 'Wrong Answer' },
  { label: 'Compile Error', value: 'Compile Error' },
  { label: 'Runtime Error', value: 'Runtime Error' },
  { label: 'Time Limit Exceeded', value: 'Time Limit Exceeded' },
  { label: 'Memory Limit Exceeded', value: 'Memory Limit Exceeded' },
  { label: 'Presentation Error', value: 'Presentation Error' },
  { label: 'Output Limit Exceeded', value: 'Output Limit Exceeded' },
  { label: 'Dangerous Operation', value: 'Dangerous Operation' },
  { label: 'System Error', value: 'System Error' },
  { label: 'Submitted Failed', value: 'Submitted Failed' },
  { label: 'Canceled', value: 'Canceled' }
]

const fetchSubmissionList = async () => {
  loading.value = true
  try {
    const params: ShowSubmissionRequest = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      sortField: 'gmtCreate',
      sortOrder: 'desc'
    }

    if (searchForm.value.problemId) {
      params.problemId = Number(searchForm.value.problemId)
    }
    if (searchForm.value.username) {
      params.username = searchForm.value.username
    }
    if (searchForm.value.language) {
      params.language = searchForm.value.language
    }
    if (searchForm.value.status) {
      params.status = searchForm.value.status as SubmissionStatusKey
    }

    const res = await getSubmissionInfo(params)
    if (res.code === 200) {
      submissionList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      Message.error(res.message || '获取评测记录失败')
    }
  } catch (error) {
    console.error('获取评测记录失败:', error)
    Message.error('获取评测记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchSubmissionList()
}

const handleReset = () => {
  searchForm.value.problemId = ''
  searchForm.value.username = ''
  searchForm.value.language = ''
  searchForm.value.status = ''
  handleSearch()
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchSubmissionList()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  pageNum.value = 1
  fetchSubmissionList()
}

const handleProblemClick = (problemId: number) => {
  router.push(`/problem/${problemId}`)
}

onMounted(() => {
  fetchSubmissionList()
})
</script>

<template>
  <div class="submission-page">
    <div class="page-header">
      <h2 class="page-title">评测记录</h2>
    </div>

    <Card class="search-card" :bordered="false">
      <Form :model="searchForm" layout="inline">
        <FormItem field="problemId">
          <Input v-model="searchForm.problemId" placeholder="题目ID" allow-clear style="width: 120px" />
        </FormItem>
        <FormItem field="username">
          <Input v-model="searchForm.username" placeholder="用户名" allow-clear style="width: 120px" />
        </FormItem>
        <FormItem field="language">
          <Select v-model="searchForm.language" :options="languageOptions" placeholder="语言" allow-clear
            style="width: 120px" />
        </FormItem>
        <FormItem field="status">
          <Select v-model="searchForm.status" :options="statusOptions" placeholder="状态" allow-clear
            style="width: 180px" />
        </FormItem>
        <FormItem>
          <Space>
            <Button type="primary" @click="handleSearch">
              <template #icon>
                <IconSearch />
              </template>
              搜索
            </Button>
            <Button @click="handleReset">
              <template #icon>
                <IconRefresh />
              </template>
              重置
            </Button>
          </Space>
        </FormItem>
      </Form>
    </Card>

    <Card class="table-card" :bordered="false">
      <Table :loading="loading" :data="submissionList" :pagination="false" stripe :bordered="false"
        class="submission-table">
        <template #columns>
          <TableColumn title="提交ID" data-index="id" :width="100" />
          <TableColumn title="用户" data-index="username" :width="120" />
          <TableColumn title="题目" :width="100">
            <template #cell="{ record }">
              <a class="problem-link" @click="handleProblemClick(record.problemId)">
                {{ record.problemId }}
              </a>
            </template>
          </TableColumn>
          <TableColumn title="状态" :width="180">
            <template #cell="{ record }">
              <Tag :color="getSubmissionStatusInfo(record.status).color">
                {{ getSubmissionStatusInfo(record.status).description }}
              </Tag>
            </template>
          </TableColumn>
          <TableColumn title="语言" :width="100">
            <template #cell="{ record }">
              <span class="language-text">{{ getLanguageText(record.language) }}</span>
            </template>
          </TableColumn>
          <TableColumn title="耗时" :width="100">
            <template #cell="{ record }">
              <span v-if="record.time !== null">{{ record.time }}ms</span>
              <span v-else class="no-data">-</span>
            </template>
          </TableColumn>
          <TableColumn title="内存" :width="100">
            <template #cell="{ record }">
              <span v-if="record.memory !== null">{{ (record.memory / 1024).toFixed(2) }}MB</span>
              <span v-else class="no-data">-</span>
            </template>
          </TableColumn>
          <TableColumn title="代码长度" data-index="codeLength" :width="100" />
          <TableColumn title="提交时间" :width="180">
            <template #cell="{ record }">
              {{ formatDate(record.gmtCreate) }}
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
.submission-page {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
}

.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.table-card {
  border-radius: 8px;
}

.problem-link {
  color: #165dff;
  cursor: pointer;
  text-decoration: none;
  font-weight: 500;
}

.problem-link:hover {
  color: #0e42d2;
  text-decoration: underline;
}

.language-text {
  font-size: 13px;
  color: #4e5969;
}

.no-data {
  color: #c9cdd4;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e5e6eb;
}
</style>
