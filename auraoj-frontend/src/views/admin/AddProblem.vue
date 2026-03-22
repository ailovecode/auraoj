<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Card,
  Button,
  Form,
  FormItem,
  Input,
  InputNumber,
  Select,
  Space,
  Row,
  Textarea,
  Col,
  Message,
  Modal,
  Form as ArcoForm
} from '@arco-design/web-vue'
import { IconLeft, IconPlus } from '@arco-design/web-vue/es/icon'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { addProblem, updateProblem, getProblemDetail } from '@/api/problem'
import { listAllTags, addTag } from '@/api/tag'
import type { ProblemAddRequest, UpdateProblemRequest } from '@/types/problem'
import type { TagInfo, AddTagReq } from '@/types/tagInfo'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const submitting = ref(false)
const loading = ref(false)
const tagOptions = ref<TagInfo[]>([])
const loadingTags = ref(false)

// 判断是编辑模式还是新增模式
const isEditMode = computed(() => !!route.params.id)
const pageTitle = computed(() => isEditMode.value ? '编辑题目' : '新建题目')

// 原始数据（用于比较哪些字段被修改）
const originalData = ref<Partial<ProblemAddRequest & { status: number }>>({})

// 新建标签弹窗相关
const addTagModalVisible = ref(false)
const addTagFormRef = ref()
const addTagForm = reactive<AddTagReq>({
  name: '',
  classification: 2
})
const addingTag = ref(false)

const addTagRules = {
  name: [{ required: true, message: '请输入标签名称' }],
  classification: [{ required: true, message: '请选择标签分类' }]
}

const classificationOptions = [
  { label: '来源', value: 1 },
  { label: '知识点', value: 2 }
]

const formData = reactive<ProblemAddRequest & { status: number }>({
  title: '',
  timeLimit: 1000,
  memoryLimit: 256,
  description: '',
  inputDesc: '',
  outputDesc: '',
  dataScope: '',
  sampleInput: '',
  sampleOutput: '',
  difficulty: 'easy',
  tagIds: [],
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入题目名称' }],
  timeLimit: [{ required: true, message: '请输入时间限制' }],
  memoryLimit: [{ required: true, message: '请输入内存限制' }],
  difficulty: [{ required: true, message: '请选择难度' }],
  status: [{ required: true, message: '请选择题目状态' }],
  tagIds: [{ required: true, message: '请选择标签' }],
  description: [{ required: true, message: '请输入题目描述' }]
}

const difficultyOptions = [
  { label: '简单', value: 'easy' },
  { label: '中等', value: 'medium' },
  { label: '困难', value: 'hard' }
]

const statusOptions = [
  { label: '公开', value: 1 },
  { label: '私有', value: 2 },
  { label: '比赛中', value: 3 }
]

const fetchTags = async (tagName?: string) => {
  loadingTags.value = true
  try {
    const res = await listAllTags(tagName)
    if (res.code === 200) {
      tagOptions.value = res.data || []
    }
  } catch (error) {
    console.error('获取标签失败:', error)
  } finally {
    loadingTags.value = false
  }
}

const fetchProblemDetail = async (problemId: number) => {
  loading.value = true
  try {
    const res = await getProblemDetail(problemId)
    if (res.code === 200 && res.data) {
      const data = res.data
      // 回显表单数据
      formData.title = data.title
      formData.timeLimit = data.timeLimit
      formData.memoryLimit = data.memoryLimit
      formData.description = data.description
      formData.inputDesc = data.inputDesc
      formData.outputDesc = data.outputDesc
      formData.dataScope = data.dataScope || ''
      formData.sampleInput = data.sampleInput || ''
      formData.sampleOutput = data.sampleOutput || ''
      formData.difficulty = data.difficulty
      formData.tagIds = data.tags?.map(tag => tag.id) || []

      // 保存原始数据用于后续比较
      originalData.value = {
        title: data.title,
        timeLimit: data.timeLimit,
        memoryLimit: data.memoryLimit,
        description: data.description,
        inputDesc: data.inputDesc,
        outputDesc: data.outputDesc,
        dataScope: data.dataScope || '',
        sampleInput: data.sampleInput || '',
        sampleOutput: data.sampleOutput || '',
        difficulty: data.difficulty,
        tagIds: data.tags?.map(tag => tag.id) || []
      }
    } else {
      Message.error(res.message || '获取题目详情失败')
    }
  } catch (error) {
    console.error('获取题目详情失败:', error)
    Message.error('获取题目详情失败')
  } finally {
    loading.value = false
  }
}

const handleTagSearch = (tagName: string) => {
  fetchTags(tagName)
}

// 打开新建标签弹窗
const handleOpenAddTagModal = () => {
  addTagForm.name = ''
  addTagForm.classification = 2
  addTagModalVisible.value = true
}

// 提交新建标签
const handleAddTagSubmit = async () => {
  try {
    await addTagFormRef.value.validate()
    addingTag.value = true

    const res = await addTag(addTagForm)
    if (res.code === 200) {
      Message.success('标签创建成功')
      addTagModalVisible.value = false
      fetchTags()
    } else {
      Message.error(res.message || '创建失败')
    }
  } catch (error) {
    console.error('创建标签失败:', error)
  } finally {
    addingTag.value = false
  }
}

// 取消新建标签
const handleAddTagCancel = () => {
  addTagModalVisible.value = false
}

// 比较两个值是否相等（考虑 undefined 的情况）
const isValueChanged = (newVal: any, oldVal: any): boolean => {
  // 如果两个都是 undefined，认为没变化
  if (newVal === undefined && oldVal === undefined) return false
  // 如果只有一个是 undefined，说明有变化
  if (newVal === undefined || oldVal === undefined) return true
  // 如果是数组，比较长度和每个元素
  if (Array.isArray(newVal) && Array.isArray(oldVal)) {
    if (newVal.length !== oldVal.length) return true
    return newVal.some((val, idx) => val !== oldVal[idx])
  }
  // 普通比较
  return newVal !== oldVal
}

// 构建更新参数，只包含变化的字段
const buildUpdateParams = (): UpdateProblemRequest | null => {
  const problemId = Number(route.params.id)
  if (!problemId) return null

  const updateData: UpdateProblemRequest = {
    id: problemId
  }

  // 比较每个字段
  if (isValueChanged(formData.title, originalData.value.title)) {
    updateData.title = formData.title
  }
  if (isValueChanged(formData.timeLimit, originalData.value.timeLimit)) {
    updateData.timeLimit = formData.timeLimit
  }
  if (isValueChanged(formData.memoryLimit, originalData.value.memoryLimit)) {
    updateData.memoryLimit = formData.memoryLimit
  }
  if (isValueChanged(formData.description, originalData.value.description)) {
    updateData.description = formData.description
  }
  if (isValueChanged(formData.inputDesc, originalData.value.inputDesc)) {
    updateData.inputDesc = formData.inputDesc
  }
  if (isValueChanged(formData.outputDesc, originalData.value.outputDesc)) {
    updateData.outputDesc = formData.outputDesc
  }
  if (isValueChanged(formData.dataScope, originalData.value.dataScope)) {
    updateData.dataScope = formData.dataScope
  }
  if (isValueChanged(formData.sampleInput, originalData.value.sampleInput)) {
    updateData.sampleInput = formData.sampleInput
  }
  if (isValueChanged(formData.sampleOutput, originalData.value.sampleOutput)) {
    updateData.sampleOutput = formData.sampleOutput
  }
  if (isValueChanged(formData.difficulty, originalData.value.difficulty)) {
    updateData.difficulty = formData.difficulty
  }
  if (isValueChanged(formData.status, originalData.value.status)) {
    updateData.status = formData.status
  }
  if (isValueChanged(formData.tagIds, originalData.value.tagIds)) {
    updateData.tagIds = formData.tagIds
  }

  // 检查是否有任何字段被修改
  const hasChanges = Object.keys(updateData).length > 1 // 除了 id 之外还有其他字段
  return hasChanges ? updateData : null
}

const handleSubmit = async () => {
  try {
    const err = await formRef.value.validate()
    if (err) return

    submitting.value = true

    if (isEditMode.value) {
      // 编辑模式：只提交修改的字段
      const updateData = buildUpdateParams()
      if (!updateData) {
        Message.warning('没有任何修改，无需保存')
        submitting.value = false
        return
      }

      const res = await updateProblem(updateData)
      if (res.code === 200) {
        Message.success('题目更新成功')
        router.push('/admin/problem')
      } else {
        Message.error(res.message || '更新失败')
      }
    } else {
      // 新增模式：提交完整数据
      const res = await addProblem(formData)
      if (res.code === 200) {
        Message.success('题目创建成功')
        router.push('/admin/problem')
      } else {
        Message.error(res.message || '创建失败')
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  fetchTags()
  // 如果是编辑模式，获取题目详情
  if (isEditMode.value) {
    const problemId = Number(route.params.id)
    if (problemId) {
      fetchProblemDetail(problemId)
    }
  }
})
</script>

<template>
  <div class="add-problem-page">
    <div class="page-header">
      <Button type="text" class="back-btn" @click="handleCancel">
        <template #icon>
          <IconLeft />
        </template>
        返回
      </Button>
      <span class="page-path">题目管理 / {{ pageTitle }}</span>
    </div>

    <div class="form-container">
      <Card class="form-card" :bordered="false">
        <Form ref="formRef" :model="formData" :rules="rules" layout="vertical" size="large" class="custom-form">

          <FormItem field="title">
            <template #label><span class="section-title">题目名称</span></template>
            <Input v-model="formData.title" placeholder="例如：A + B Problem" />
          </FormItem>

          <FormItem field="tagIds">
            <template #label>
              <Space align="center" size="large">
                <span class="section-title">题目标签</span>
                <Button type="primary" size="mini" @click="handleOpenAddTagModal">
                  <template #icon>
                    <IconPlus />
                  </template>
                  新建标签
                </Button>
              </Space>
            </template>
            <Select v-model="formData.tagIds" :options="tagOptions.map(tag => ({ label: tag.name, value: tag.id }))"
              multiple placeholder="请搜索并选择标签" :loading="loadingTags" @search="handleTagSearch" filterable allow-clear />
          </FormItem>

          <Row :gutter="24">
            <Col :span="6">
              <FormItem field="difficulty">
                <template #label><span class="section-title">难度</span></template>
                <Select v-model="formData.difficulty" :options="difficultyOptions" placeholder="请选择" />
              </FormItem>
            </Col>
            <Col :span="6">
              <FormItem field="status">
                <template #label><span class="section-title">状态</span></template>
                <Select v-model="formData.status" :options="statusOptions" placeholder="请选择" />
              </FormItem>
            </Col>
            <Col :span="6">
              <FormItem field="timeLimit">
                <template #label><span class="section-title">时间限制 (ms)</span></template>
                <InputNumber v-model="formData.timeLimit" :min="1" :max="10000" class="full-width-input" />
              </FormItem>
            </Col>
            <Col :span="6">
              <FormItem field="memoryLimit">
                <template #label><span class="section-title">内存限制 (MB)</span></template>
                <InputNumber v-model="formData.memoryLimit" :min="1" :max="1024" class="full-width-input" />
              </FormItem>
            </Col>
          </Row>

          <FormItem field="description">
            <template #label><span class="section-title">题目描述</span></template>
            <div class="editor-wrapper">
              <MdEditor v-model="formData.description" placeholder="请输入题目描述（支持 Markdown 和 LaTeX 公式）"
                :toolbars-exclude="['github']" :editor-style="{ height: '400px' }" />
            </div>
          </FormItem>

          <FormItem field="inputDesc">
            <template #label><span class="section-title">输入描述</span></template>
            <div class="editor-wrapper">
              <MdEditor v-model="formData.inputDesc" placeholder="描述该题目的输入格式" :toolbars-exclude="['github']"
                :editor-style="{ height: '200px' }" />
            </div>
          </FormItem>

          <FormItem field="outputDesc">
            <template #label><span class="section-title">输出描述</span></template>
            <div class="editor-wrapper">
              <MdEditor v-model="formData.outputDesc" placeholder="描述该题目的输出格式" :toolbars-exclude="['github']"
                :editor-style="{ height: '200px' }" />
            </div>
          </FormItem>

          <FormItem field="dataScope">
            <template #label><span class="section-title">数据范围与提示</span></template>
            <div class="editor-wrapper">
              <MdEditor v-model="formData.dataScope" placeholder="例如：对于 100% 的数据，1 <= N <= 10^5"
                :toolbars-exclude="['github']" :editor-style="{ height: '150px' }" />
            </div>
          </FormItem>

          <Row :gutter="24">
            <Col :span="12">
              <FormItem field="sampleInput">
                <template #label><span class="section-title">样例输入</span></template>
                <Textarea v-model="formData.sampleInput" placeholder="请输入真实的测试样例输入"
                  :auto-size="{ minRows: 6, maxRows: 12 }" class="monospace-input" />
              </FormItem>
            </Col>
            <Col :span="12">
              <FormItem field="sampleOutput">
                <template #label><span class="section-title">样例输出</span></template>
                <Textarea v-model="formData.sampleOutput" placeholder="请输入对应的样例输出"
                  :auto-size="{ minRows: 6, maxRows: 12 }" class="monospace-input" />
              </FormItem>
            </Col>
          </Row>

        </Form>
      </Card>
    </div>

    <!-- 新建标签弹窗 -->
    <Modal v-model:visible="addTagModalVisible" title="新建标签" :mask-closable="false" :closable="true" width="480px"
      @before-ok="handleAddTagSubmit" @cancel="handleAddTagCancel">
      <Form ref="addTagFormRef" :model="addTagForm" :rules="addTagRules" layout="vertical">
        <FormItem field="name" label="标签名称">
          <Input v-model="addTagForm.name" placeholder="请输入标签名称，例如：动态规划" />
        </FormItem>
        <FormItem field="classification" label="标签分类">
          <Select v-model="addTagForm.classification" :options="classificationOptions" placeholder="请选择标签分类" />
        </FormItem>
      </Form>
    </Modal>

    <div class="page-footer">
      <Space size="medium" justify="end">
        <Button size="large" @click="handleCancel">取消</Button>
        <Button size="large" type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEditMode ? '保存修改' : '提交并保存' }}
        </Button>
      </Space>
    </div>
  </div>
</template>

<style scoped>
.add-problem-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f2f3f5;
}

/* 顶部导航样式 */
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid var(--color-border-2);
  position: sticky;
  top: 0;
  z-index: 100;
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

/* 居中限制最大宽度，让大屏下不会拉伸得过长 */
.form-container {
  flex: 1;
  padding: 24px;
  display: flex;
  justify-content: center;
}

.form-card {
  width: 100%;
  max-width: 1200px;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.02);
  padding: 16px 24px;
}

/* ----- 核心表单样式优化 ----- */
.custom-form :deep(.arco-form-item) {
  margin-bottom: 32px;
}

/* 自定义标黑标题：带左侧蓝色点缀线 */
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  position: relative;
  padding-left: 10px;
  line-height: 1.2;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background-color: rgb(var(--primary-6));
  border-radius: 2px;
}

/* Markdown 编辑器外壳修饰，避免太突兀 */
.editor-wrapper {
  border: 1px solid var(--color-border-2);
  border-radius: 4px;
  overflow: hidden;
  width: 100%;
}

/* 底部操作区 */
.page-footer {
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  padding: 16px 40px;
  background: #fff;
  border-top: 1px solid var(--color-border-2);
  z-index: 10;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

/* 等宽字体，用于样例输入输出的对齐 */
.monospace-input :deep(textarea) {
  font-family: 'JetBrains Mono', 'Consolas', 'Fira Code', monospace;
  background-color: #fafafa;
  border: 1px solid var(--color-border-2);
}
</style>
