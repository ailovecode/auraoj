import type { TagInfo } from "./tagInfo"

// 难度等级类型
export type DifficultyLevel = "easy" | "medium" | "hard"

// 基础题目信息
export interface BaseProblemInfo {
  id: number
  title: string
  description: string
  inputDesc: string
  outputDesc: string
  dataScope: string
  timeLimit: number
  memoryLimit: number
  difficulty: DifficultyLevel
  submitNum: number
  acceptNum: number
  passRate: number
  favourNum: number
  sampleOutput: string
  sampleInput: string
  tags: TagInfo[]
}

// 后台管理题目列表信息
export interface AdminProblemInfo extends BaseProblemInfo {
  status: number
  creatorId: number
  judgeType: string
  gmtCreate: string
  gmtModified: string
}

// 分页结果
export interface ProblemListData {
  pageNum: number
  pageSize: number
  total: number
  totalPages: number
  list: AdminProblemInfo[]
  hasPrevious: boolean
  hasNext: boolean
}

// 查询结果
export interface QueryAllProblemRes {
  code: number
  data: ProblemListData
  message: string
}

// 问题添加请求
export interface ProblemAddRequest {
  title: string;
  timeLimit: number;
  memoryLimit: number;
  description: string;
  inputDesc: string;
  outputDesc: string;
  dataScope?: string;
  sampleInput?: string;
  sampleOutput?: string;
  difficulty: DifficultyLevel;
  tagIds?: number[];
}

// 问题添加结果
export interface ProblemAddRes {
  code: number
  data: number
  message: string
}

// 问题更新请求（所有字段可选，但必须有题目ID）
export interface UpdateProblemRequest {
  id: number
  title?: string
  timeLimit?: number
  memoryLimit?: number
  description?: string
  inputDesc?: string
  outputDesc?: string
  dataScope?: string
  sampleInput?: string
  sampleOutput?: string
  difficulty?: DifficultyLevel
  tagIds?: number[]
  status?: number
}

export interface ProblemSearchRequest {
  title?: string
  difficulty?: DifficultyLevel
  tagId?: number
}

export interface GetProblemRes {
  code: number
  data: BaseProblemInfo
  message: string
}

export interface DeleteProblemRes {
  code: number
  data: boolean
  message: string
}
