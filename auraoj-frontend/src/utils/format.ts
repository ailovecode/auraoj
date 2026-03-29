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
  'Pending': { color: 'gray', description: 'Pending' },
  'Accepted': { color: 'green', description: 'Accepted' },
  'Wrong Answer': { color: 'red', description: 'Wrong Answer' },
  'Compile Error': { color: 'orange', description: 'Compile Error' },
  'Runtime Error': { color: 'gold', description: 'Runtime Error' },
  'Time Limit Exceeded': { color: 'cyan', description: 'Time Limit Exceeded' },
  'Memory Limit Exceeded': { color: 'purple', description: 'Memory Limit Exceeded' },
  'Presentation Error': { color: 'pink', description: 'Presentation Error' },
  'Output Limit Exceeded': { color: 'magenta', description: 'Output Limit Exceeded' },
  'Dangerous Operation': { color: 'brown', description: 'Dangerous Operation' },
  'System Error': { color: 'default', description: 'System Error' },
  'Submitted Failed': { color: 'red', description: 'Submitted Failed' },
  'Canceled': { color: 'gray', description: 'Canceled' }
} as const

export type SubmissionStatusKey = keyof typeof SUBMISSION_STATUS

export const getSubmissionStatusInfo = (status: string): { color: string; description: string } => {
  return SUBMISSION_STATUS[status as SubmissionStatusKey] || { color: 'gray', description: 'Unknown' }
}
