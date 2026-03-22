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
