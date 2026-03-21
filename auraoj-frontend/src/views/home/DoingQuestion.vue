<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Button, Space, Tag, TypographyTitle, Select, Tabs, TabPane, Textarea, Message
} from '@arco-design/web-vue'
import { IconRefresh, IconPlayArrow } from '@arco-design/web-vue/es/icon'
import { VueMonacoEditor } from '@guolao/vue-monaco-editor'
import { getProblemDetail } from '@/api/problem'
import type { BaseProblemInfo, DifficultyLevel } from '@/types/problem'

// 引入 marked 和 KaTeX 插件
import { marked } from 'marked'
import markedKatex from 'marked-katex-extension'
import 'katex/dist/katex.min.css'

// 注册 KaTeX 插件到 marked
marked.use(markedKatex({
  throwOnError: false,
  displayMode: true
}))

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const problemData = ref<BaseProblemInfo | null>(null)
const selectedLanguage = ref('java')
const customInput = ref('')
const runOutput = ref('')
const activeTab = ref('testcases')

const languageOptions = [
  { label: 'Java', value: 'java' },
  { label: 'C++', value: 'cpp' },
  { label: 'Python', value: 'python' },
  { label: 'JavaScript', value: 'javascript' }
]

const languageMap: Record<string, string> = {
  java: 'java',
  cpp: 'cpp',
  python: 'python',
  javascript: 'javascript'
}

const defaultCode: Record<string, string> = {
  java: `public class Solution {
    public static void main(String[] args) {
        // 请在此编写代码
    }
}`,
  cpp: `#include <iostream>
using namespace std;

int main() {
    // 请在此编写代码
    return 0;
}`,
  python: `# 请在此编写代码
def main():
    pass

if __name__ == "__main__":
    main()`,
  javascript: `// 请在此编写代码
function solution() {

}`
}

const editorCode = ref(defaultCode.java)

const getDifficultyText = (difficulty?: DifficultyLevel): string => {
  switch (difficulty) {
    case 'easy': return '简单'
    case 'medium': return '中等'
    case 'hard': return '困难'
    default: return '未知'
  }
}

const getDifficultyColor = (difficulty?: DifficultyLevel): string => {
  switch (difficulty) {
    case 'easy': return 'green'
    case 'medium': return 'orange'
    case 'hard': return 'red'
    default: return 'gray'
  }
}

const fetchProblemDetail = async () => {
  const problemId = route.params.id as string
  if (!problemId) {
    Message.error('题目ID不存在')
    router.push('/problem')
    return
  }

  loading.value = true
  try {
    const res = await getProblemDetail(Number(problemId))
    if (res.code === 200 && res.data) {
      problemData.value = res.data

      if (res.data.sampleInput) {
        customInput.value = res.data.sampleInput
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

const handleLanguageChange = (value: string) => {
  selectedLanguage.value = value
  editorCode.value = defaultCode[value as keyof typeof defaultCode] || defaultCode.java
}

const handleResetCode = () => {
  editorCode.value = defaultCode[selectedLanguage.value as keyof typeof defaultCode] || defaultCode.java
  Message.success('代码已重置')
}

const handleRestoreSample = () => {
  if (problemData.value?.sampleInput) {
    customInput.value = problemData.value.sampleInput
    Message.success('已恢复输入样例')
  } else {
    Message.warning('当前题目无输入样例')
  }
}

const handleRunCode = () => {
  activeTab.value = 'result'
  runOutput.value = '正在运行代码...\n\n[模拟输出]\nHello, World!\nExecution Time: 12ms'
  Message.info('代码运行中（模拟）')
}

const handleSubmitCode = () => {
  Message.loading('正在提交代码...')
  setTimeout(() => {
    Message.success('提交成功！正在判题中...')
  }, 1000)
}

// const handleBack = () => {
//   router.push('/problem')
// }

onMounted(() => {
  fetchProblemDetail()
})

const renderMarkdown = (text?: string) => {
  if (!text) return ''
  return marked.parse(text, { breaks: true }) as string
}
</script>

<template>
  <div class="doing-question-page">
    <div class="main-container">
      <a-split :default-size="0.45" :min="0.2" :max="0.7" direction="horizontal" class="horizontal-split">
        <template #first>
          <div class="card-panel left-panel">

            <div class="problem-content" v-if="problemData">
              <div class="panel-header">

                <TypographyTitle :heading="3" class="problem-title">
                  {{ problemData.id }}. {{ problemData.title }}
                </TypographyTitle>

                <Space :size="8" align="center" class="problem-tags">
                  <Tag :color="getDifficultyColor(problemData.difficulty)" class="lc-tag lc-difficulty-tag">
                    {{ getDifficultyText(problemData.difficulty) }}
                  </Tag>
                  <Tag class="lc-tag">时间: {{ problemData.timeLimit }}ms</Tag>
                  <Tag class="lc-tag">内存: {{ problemData.memoryLimit }}MB</Tag>
                </Space>
              </div>
              <div class="md-content" v-html="renderMarkdown(problemData.description)"></div>

              <div class="section-title" v-if="problemData.inputDesc">输入格式</div>
              <div class="md-content" v-if="problemData.inputDesc" v-html="renderMarkdown(problemData.inputDesc)"></div>

              <div class="section-title" v-if="problemData.outputDesc">输出格式</div>
              <div class="md-content" v-if="problemData.outputDesc" v-html="renderMarkdown(problemData.outputDesc)">
              </div>

              <div class="section-title" v-if="problemData.dataScope">数据范围</div>
              <div class="md-content" v-if="problemData.dataScope" v-html="renderMarkdown(problemData.dataScope)"></div>

              <div class="section-title" v-if="problemData.sampleInput">输入样例：</div>
              <div class="sample-box" v-if="problemData.sampleInput">
                <pre>{{ problemData.sampleInput }}</pre>
              </div>

              <div class="section-title" v-if="problemData.sampleOutput">输出样例：</div>
              <div class="sample-box" v-if="problemData.sampleOutput">
                <pre>{{ problemData.sampleOutput }}</pre>
              </div>
            </div>
            <div v-else-if="loading" class="loading-state">
              <div class="loading-text">拼命加载中...</div>
            </div>
          </div>
        </template>

        <template #second>
          <a-split :default-size="0.65" :min="0.3" :max="0.85" direction="vertical" class="vertical-split">
            <template #first>
              <div class="card-panel right-up-panel">
                <div class="editor-header">
                  <Select v-model="selectedLanguage" :options="languageOptions" size="small" class="lc-select"
                    @change="handleLanguageChange" />
                  <Button size="small" type="text" @click="handleResetCode" class="lc-icon-btn">
                    <template #icon>
                      <IconRefresh />
                    </template>
                  </Button>
                </div>
                <div class="editor-container">
                  <VueMonacoEditor v-model:value="editorCode" :language="languageMap[selectedLanguage]" theme="vs-dark"
                    :options="{
                      automaticLayout: true,
                      fontSize: 14,
                      minimap: { enabled: false },
                      scrollBeyondLastLine: false,
                      renderLineHighlight: 'all',
                      tabSize: 4,
                      fontFamily: `'JetBrains Mono', Consolas, monospace`
                    }" class="monaco-wrapper" />
                </div>
              </div>
            </template>

            <template #second>
              <div class="card-panel right-down-panel">
                <Tabs v-model:active-key="activeTab" class="lc-tabs">
                  <TabPane key="testcases" title="测试用例">
                    <div class="tab-content-wrapper">
                      <div class="test-case-header">
                        <div class="test-case-label">输入</div>
                        <Button type="text" size="mini" @click="handleRestoreSample" class="restore-btn">
                          <template #icon>
                            <IconRefresh />
                          </template>
                          使用样例
                        </Button>
                      </div>
                      <Textarea v-model="customInput" placeholder="请输入自定义测试用例..." class="lc-textarea"
                        :auto-size="{ minRows: 3, maxRows: 10 }" />
                    </div>
                  </TabPane>
                  <TabPane key="result" title="执行结果">
                    <div class="tab-content-wrapper">
                      <pre class="result-display">{{ runOutput || '请先运行代码' }}</pre>
                    </div>
                  </TabPane>
                </Tabs>

                <div class="action-footer">
                  <div class="left-actions">
                    <Button type="text" class="console-btn">控制台</Button>
                  </div>
                  <div class="right-actions">
                    <Button class="lc-run-btn" @click="handleRunCode">
                      <template #icon>
                        <IconPlayArrow />
                      </template>
                      运行
                    </Button>
                    <Button class="lc-submit-btn" type="primary" @click="handleSubmitCode">
                      提交
                    </Button>
                  </div>
                </div>
              </div>
            </template>
          </a-split>
        </template>
      </a-split>
    </div>
  </div>
</template>

<style scoped>
/* ==================== 1. 全局与布局基座 ==================== */
.doing-question-page {
  height: calc(100vh - 60px);
  width: 100%;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  overflow: hidden;
  box-sizing: border-box;
}

/* ==================== 2. 左侧：题目描述（LeetCode 现代化风格） ==================== */
.panel-header {
  padding: 20px 24px 16px 24px;
  background: #ffffff;
  border-bottom: 1px solid #ffffff;
}

/* 融入左侧的返回按钮 */
/* .inner-back-btn {
  color: #86909c;
  padding: 0;
  margin-bottom: 12px;
  font-size: 13px;
}

.inner-back-btn:hover {
  color: #165dff;
  background: transparent;
} */

/* 题目大标题 */
.problem-title {
  margin: 0 0 16px 0 !important;
  font-size: 22px !important;
  font-weight: 600;
  color: #262626;
}

/* 标签组容器 */
.problem-tags {
  margin-bottom: 4px;
}

/* 图二同款的深度圆角标签 */
.lc-tag {
  border-radius: 12px;
  /* 变成胶囊形状 */
  padding: 2px 12px;
  font-size: 13px;
  font-weight: 500;
  border: none;
  background-color: #f2f3f5;
  /* 默认浅灰底色 */
  color: #4e5969;
}

/* 覆盖 Arco 默认的主题色标签样式，使其更柔和 */
.lc-difficulty-tag {
  border-radius: 12px;
}

/* --- 下方的 .problem-content 样式保持不变 --- */
.problem-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  color: #333;
  font-size: 15px;
  line-height: 1.8;
}

/* ... 后面的不变 */

.main-container {
  flex: 1;
  padding: 8px;
  overflow: hidden;
  box-sizing: border-box;
}

.card-panel {
  background: #ffffff;
  border-radius: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 拖拽组件强制撑满与样式美化 */
.horizontal-split,
.vertical-split {
  height: 100%;
  width: 100%;
}

:deep(.arco-split-trigger) {
  background-color: transparent !important;
}

:deep(.arco-split-trigger-icon-wrapper) {
  background-color: #e5e6eb;
  border-radius: 4px;
}

:deep(.arco-split-trigger:hover .arco-split-trigger-icon-wrapper) {
  background-color: #165dff;
}

/* ==================== 2. 左侧：题目描述（AcWing 极简风格） ==================== */
.panel-header {
  padding: 12px 16px;
  border-bottom: 1px solid #f2f3f5;
  background: #ffffff;
}

.lc-tag {
  border-radius: 4px;
  font-weight: 500;
}

.problem-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  color: #333;
  font-size: 15px;
  line-height: 1.8;
}

.section-title {
  font-weight: bold;
  font-size: 16px;
  margin: 28px 0 12px 0;
  color: #262626;
}

.problem-content>div:first-child {
  margin-top: 0;
}

/* Markdown 解析样式穿透 */
.md-content {
  margin-bottom: 16px;
}

.md-content :deep(p) {
  margin: 0 0 12px 0;
}

.md-content :deep(code) {
  background-color: #f7f8fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: 14px;
  color: #e64a19;
}

.sample-box {
  background-color: #f7f8fa;
  border-radius: 4px;
  padding: 12px 16px;
  margin-bottom: 24px;
}

.sample-box pre {
  margin: 0;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: 14px;
  color: #1d2129;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* ==================== 3. 右上：代码编辑器 ==================== */
.right-up-panel {
  background: #1e1e1e;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: #262626;
  border-bottom: 1px solid #333;
}

.lc-select :deep(.arco-select-view) {
  background: #333;
  border: none;
  color: #e5e6eb;
  border-radius: 4px;
}

.lc-icon-btn {
  color: #86909c;
}

.lc-icon-btn:hover {
  background: #333;
  color: #fff;
}

.editor-container {
  flex: 1;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.monaco-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

/* ==================== 4. 右下：调试台 (极简版) ==================== */
.right-down-panel {
  position: relative;
}

.lc-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 简化点 1：Tab栏背景改为纯白，去除灰底 */
.lc-tabs :deep(.arco-tabs-nav) {
  padding: 0 16px;
  background: #ffffff;
  border-bottom: 1px solid #f2f3f5;
  flex-shrink: 0;
}

.lc-tabs :deep(.arco-tabs-content) {
  padding: 0;
  flex: 1;
  overflow: hidden;
}

.lc-tabs :deep(.arco-tabs-pane) {
  height: 100%;
}

.tab-content-wrapper {
  padding: 16px 16px 60px 16px;
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
}

/* --- 测试用例输入区专属样式 --- */
.test-case-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.test-case-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.restore-btn {
  color: #165dff;
  font-size: 13px;
  padding: 0 4px;
}

.restore-btn:hover {
  background-color: transparent;
  color: #0e42d2;
}

.lc-textarea {
  width: 100%;
  background-color: #ffffff;
  border: 1px solid #a8c7fa;
  border-radius: 6px;
  transition: all 0.2s;
}

.lc-textarea:hover,
.lc-textarea.arco-textarea-wrapper-focus {
  background-color: #ffffff;
  border-color: #165dff;
  box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
}

.lc-textarea :deep(textarea) {
  border: none;
}

.lc-textarea :deep(.arco-textarea) {
  padding: 12px 16px;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
  resize: none;
}

/* --- 简化点 2：纯净版执行结果展示 --- */
.result-display {
  margin: 0;
  padding: 0;
  /* 靠外层 wrapper 的 padding 撑开 */
  background: transparent;
  /* 去除灰色底色 */
  border-radius: 0;
  /* 去除圆角 */
  font-family: 'JetBrains Mono', Consolas, monospace;
  color: #333;
  /* 字体颜色加深，在白底上更清晰 */
  white-space: pre-wrap;
  word-wrap: break-word;
}

.action-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #f2f3f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.console-btn {
  color: #4e5969;
  font-size: 13px;
}

.lc-run-btn {
  background: #f2f3f5;
  color: #1d2129;
  border-radius: 6px;
  margin-right: 12px;
  font-weight: 500;
}

.lc-run-btn:hover {
  background: #e5e6eb;
}

.lc-submit-btn {
  border-radius: 6px;
  font-weight: 500;
  background-color: #2db55d;
}

.lc-submit-btn:hover {
  background-color: #249e4f;
}
</style>
