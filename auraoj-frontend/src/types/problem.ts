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
  judgeConfig: string
  difficulty: DifficultyLevel
  submitNum: number
  acceptNum: number
  passRate: number
  favourNum: number
  sampleOutput: string
  sampleInput: string
  tags: TagInfo[]
}

// 标签信息
export interface TagInfo {
  id: number
  name: string
  classification: number
  creatorId: number
  gmtCreate: string
  gmtModified: string
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

export interface ProblemSearchRequest {
  title?: string
  difficulty?: DifficultyLevel
  tagId?: number
}
