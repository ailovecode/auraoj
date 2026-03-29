import type { StatusKey } from "./problem";

export interface ShowSubmissionRequest {
  pageNum?: number;
  pageSize?: number;
  sortField?: string;
  sortOrder?: string;
  id?: number;
  username?: string;
  problemId?: number;
  contestId?: number;
  language?: string;
  status?: StatusKey;
  pattern?: number;
}

export interface ShowSubmissionInfo {
  id: number;
  username: string;
  time: number | null;
  memory: number | null;
  problemId: number;
  language: string;
  status: StatusKey;
  codeLength: number;
  aiAnalyse: string | null;
  gmtCreate: Date;
}

export interface SubmissionRes {
  pageNum: number
  pageSize: number
  total: number
  totalPages: number
  list: ShowSubmissionInfo[]
  hasPrevious: boolean
  hasNext: boolean
}
