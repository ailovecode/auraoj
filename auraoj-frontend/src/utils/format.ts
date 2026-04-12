import type { DifficultyLevel } from '@/types/problem'

export type { DifficultyLevel }

export const getDifficultyText = (difficulty?: DifficultyLevel): string => {
  switch (difficulty) {
    case 'easy': return '简单'
    case 'medium': return '中等'
    case 'hard': return '困难'
    default: return '未知'
  }
}

export const getDifficultyColor = (difficulty?: DifficultyLevel): string => {
  switch (difficulty) {
    case 'easy': return 'green'
    case 'medium': return 'orange'
    case 'hard': return 'red'
    default: return 'gray'
  }
}

export const formatDate = (dateStr?: string): string => {
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

export type ProblemStatus = 1 | 2 | 3

export const getProblemStatusText = (status?: number): string => {
  switch (status) {
    case 1: return '公开'
    case 2: return '私有'
    case 3: return '比赛中'
    default: return '未知'
  }
}

export const getProblemStatusColor = (status?: number): string => {
  switch (status) {
    case 1: return 'green'
    case 2: return 'red'
    case 3: return 'orange'
    default: return 'gray'
  }
}

export type UserStatus = 0 | 1

export const getUserStatusText = (status?: number): string => {
  return status === 1 ? '已禁用' : '正常'
}

export const getUserStatusColor = (status?: number): string => {
  return status === 1 ? 'red' : 'green'
}

export const getGenderText = (gender?: number): string => {
  switch (gender) {
    case 0: return '女'
    case 1: return '男'
    default: return '未知'
  }
}

export const getRoleText = (role?: string): string => {
  switch (role) {
    case 'admin': return '管理员'
    case 'teacher': return '老师'
    default: return '学生'
  }
}

export const getRoleTagColor = (role?: string): string => {
  switch (role) {
    case 'admin': return 'red'
    case 'teacher': return 'orange'
    default: return 'arcoblue'
  }
}

export const getLanguageText = (language: string): string => {
  const langMap: Record<string, string> = {
    java: 'Java',
    cpp: 'C++',
    python: 'Python',
    javascript: 'JavaScript'
  }
  return langMap[language] || language
}

export const SUBMISSION_STATUS = {
  // --- 终态：成功 ---
  'Accepted': { color: '#52c41a', description: 'Accepted' }, // 成功绿

  // --- 终态：逻辑/运行错误 ---
  'Wrong Answer': { color: '#ff4d4f', description: 'Wrong Answer' }, // 逻辑错误红
  'Presentation Error': { color: '#fa8c16', description: 'Presentation Error' }, // 格式错误橙

  // --- 终态：资源超限 ---
  'Time Limit Exceeded': { color: '#13c2c2', description: 'Time Limit Exceeded' }, // 运行超时青
  'Memory Limit Exceeded': { color: '#722ed1', description: 'Memory Limit Exceeded' }, // 内存溢出紫
  'Output Limit Exceeded': { color: '#eb2f96', description: 'Output Limit Exceeded' }, // 输出超限品红

  // --- 终态：执行异常 ---
  'Runtime Error': { color: '#faad14', description: 'Runtime Error' }, // 运行崩溃金
  'Compile Error': { color: '#d4b106', description: 'Compile Error' }, // 编译失败黄
  'Dangerous Operation': { color: '#820014', description: 'Dangerous Operation' }, // 危险操作深红

  // --- 中间态：处理中 ---
  'Pending': { color: '#bfbfbf', description: 'Pending' }, // 等待中灰色
  'Compiling': { color: '#1890ff', description: 'Compiling' }, // 编译中蓝色
  'Judging': { color: '#1890ff', description: 'Judging' }, // 评测中蓝色

  // --- 异常状态 ---
  'System Error': { color: '#262626', description: 'System Error' }, // 系统故障黑
  'Submitted Failed': { color: '#595959', description: 'Submitted Failed' }, // 提交失败深灰
  'Canceled': { color: '#d9d9d9', description: 'Canceled' } // 已取消淡灰
} as const;

export type SubmissionStatusKey = keyof typeof SUBMISSION_STATUS

export const getSubmissionStatusInfo = (status: string): { color: string; description: string } => {
  return SUBMISSION_STATUS[status as SubmissionStatusKey] || { color: 'gray', description: 'Unknown' }
}
