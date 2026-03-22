<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Card,
  Input,
  Button,
  Tag,
  Table,
  TableColumn,
  Pagination,
  Message
} from '@arco-design/web-vue'
import { searchProblem } from '@/api/problem'
import type { AdminProblemInfo, ProblemSearchRequest } from '@/types/problem'
import { listAllTags } from '@/api/tag'
import type { TagInfo } from '@/types/tagInfo'
import { getDifficultyText, getDifficultyColor } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const problemList = ref<AdminProblemInfo[]>([])
const tagOptions = ref<TagInfo[]>([])
const difficultyFilter = ref('')

const searchForm = ref<ProblemSearchRequest>({
  title: '',
  difficulty: undefined,
  tagId: undefined
})

const difficultyOptions = [
  { label: '全部难度', value: '' },
  { label: '简单', value: "easy" },
  { label: '中等', value: "medium" },
  { label: '困难', value: "hard" }
]

const filterOptions = [
  { text: '全部难度', value: '' },
  { text: '简单', value: 'easy' },
  { text: '中等', value: 'medium' },
  { text: '困难', value: 'hard' }
]

const fetchProblemList = async () => {
  loading.value = true
  try {
    const res = await searchProblem(pageNum.value, pageSize.value, searchForm.value)
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

const fetchTags = async () => {
  try {
    const res = await listAllTags()
    if (res.code === 200) {
      tagOptions.value = res.data || []
    }
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchProblemList()
}

const handleReset = () => {
  searchForm.value.title = ''
  searchForm.value.difficulty = undefined
  searchForm.value.tagId = undefined
  difficultyFilter.value = ''
  pageNum.value = 1
  fetchProblemList()
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

const handleProblemClick = (problem: AdminProblemInfo) => {
  router.push(`/problem/${problem.id}`)
}

const handleDifficultyFilter = (value: string) => {
  difficultyFilter.value = value
}

const filteredProblemList = computed(() => {
  return problemList.value.filter((problem) => {
    const matchKeyword = !searchForm.value.title ||
      problem.title.toLowerCase().includes(searchForm.value.title.toLowerCase()) ||
      problem.id.toString().includes(searchForm.value.title)
    const matchDifficulty = !searchForm.value.difficulty || problem.difficulty === searchForm.value.difficulty
    const matchTag = !searchForm.value.tagId ||
      (problem.tags && problem.tags.some((tag) => tag.id === searchForm.value.tagId))
    const matchColumnFilter = !difficultyFilter.value || problem.difficulty === difficultyFilter.value
    return matchKeyword && matchDifficulty && matchTag && matchColumnFilter
  })
})

onMounted(() => {
  fetchProblemList()
  fetchTags()
})
</script>

<template>
  <div class="problem-list-page">
    <Card class="search-card" :bordered="false">
      <div class="search-content">
        <div class="search-input-wrapper">
          <Input.Search v-model="searchForm.title" placeholder="输入题号/题目名按回车搜索..." :search-button="false"
            @search="handleSearch" class="search-input" />
          <Button @click="handleSearch">搜索</Button>
          <Button @click="handleReset">重置</Button>
        </div>
      </div>
    </Card>

    <Card class="table-card" :bordered="false">
      <Table :loading="loading" :data="filteredProblemList" :pagination="false" stripe row-key="id" :scroll="{ x: 800 }"
        class="problem-table">
        <template #columns>
          <TableColumn title="编号" data-index="id" width="100" />
          <TableColumn title="题目名称" :width="400">
            <template #cell="{ record }">
              <div class="problem-title-cell" @click="handleProblemClick(record)">
                <span class="problem-title">{{ record.title }}</span>
              </div>
            </template>
          </TableColumn>
          <TableColumn title="标签" :width="200">
            <template #cell="{ record }">
              <div class="tags-cell">
                <Tag v-for="tag in record.tags" :key="tag.id" size="small" color="arcoblue" class="problem-tag">
                  {{ tag.name }}
                </Tag>
              </div>
            </template>
          </TableColumn>
          <TableColumn title="难度" data-index="difficulty" width="120" :filterable="{
            filters: filterOptions,
            filter: (value: string) => handleDifficultyFilter(value)
          }">
            <template #cell="{ record }">
              <Tag :color="getDifficultyColor(record.difficulty)" size="small">
                {{ getDifficultyText(record.difficulty) }}
              </Tag>
            </template>
          </TableColumn>
          <TableColumn title="通过/提交" width="120">
            <template #cell="{ record }">
              <span class="submit-stats">
                <span class="accept">{{ record.acceptNum }}</span>
                <span class="separator">/</span>
                <span class="submit">{{ record.submitNum }}</span>
              </span>
            </template>
          </TableColumn>
          <TableColumn title="通过率" width="100">
            <template #cell="{ record }">
              <span class="pass-rate"
                :style="{ color: record.passRate >= 50 ? '#00b42a' : record.passRate >= 20 ? '#ff7d00' : '#f53f3f' }">
                {{ (record.passRate || 0).toFixed(1) }}%
              </span>
            </template>
          </TableColumn>
        </template>
      </Table>

      <div class="pagination-wrapper">
        <Pagination :total="total" :current="pageNum" :page-size="pageSize" :show-total="true" :show-page-size="true"
          :page-size-options="[10, 20, 50, 100]" @change="handlePageChange" @page-size-change="handlePageSizeChange"
          size="large" />
      </div>
    </Card>
  </div>
</template>

<style scoped>
.problem-list-page {
  max-width: 1200px;
  margin: 0 auto;
}

.search-card {
  border-radius: 16px;
  margin-bottom: 20px;
  padding: 24px;
}

.search-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-input-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
}

.search-input :deep(.arco-input-group) {
  border-radius: 24px;
}

.table-card {
  border-radius: 16px;
}

.problem-table :deep(.arco-table-tr) {
  cursor: pointer;
}

.problem-table :deep(.arco-table-tr:hover) {
  background-color: #f2f3f5;
}

.problem-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.problem-title {
  color: #165dff;
  font-weight: 500;
  cursor: pointer;
}

.problem-title:hover {
  text-decoration: underline;
}

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.problem-tag {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.submit-stats {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
}

.accept {
  color: #00b42a;
}

.separator {
  color: #86909c;
  margin: 0 2px;
}

.submit {
  color: #4e5969;
}

.pass-rate {
  font-weight: 500;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e6eb;
}
</style>
