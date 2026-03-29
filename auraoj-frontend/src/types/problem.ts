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



// 提交

export const SubmissionStatusEnum = {
  PENDING: { status: 'Pending', description: '待评判' },
  ACCEPTED: { status: 'Accepted', description: '通过' },
  WRONG_ANSWER: { status: 'Wrong Answer', description: '答案错误' },
  COMPILE_ERROR: { status: 'Compile Error', description: '编译错误' },
  MEMORY_LIMIT_EXCEEDED: { status: 'Memory Limit Exceeded', description: '内存超限' },
  TIME_LIMIT_EXCEEDED: { status: 'Time Limit Exceeded', description: '超时' },
  PRESENTATION_ERROR: { status: 'Presentation Error', description: '格式错误' },
  OUTPUT_LIMIT_EXCEEDED: { status: 'Output Limit Exceeded', description: '输出超限' },
  DANGEROUS_OPERATION: { status: 'Dangerous Operation', description: '危险操作' },
  RUNTIME_ERROR: { status: 'Runtime Error', description: '运行时错误' },
  SYSTEM_ERROR: { status: 'System Error', description: '系统错误' },
} as const;

export type StatusKey = keyof typeof SubmissionStatusEnum;

export interface SubmitResponse {
  id: number;
  userId: number;
  problemId: number;
  contestId?: number;
  language: string;
  status: StatusKey;
  pattern: number;
  gmtCreate: Date;
}

export interface SubmitRequest {
  userId: number;
  problemId: number;
  contestId?: number;
  code: string;
  language: string;
  status?: StatusKey;
  aiAnalyse?: string;
  judgeSummary?: string;
  firstErrorCase?: string;
  compileLog?: string;
  pattern: number;
}
