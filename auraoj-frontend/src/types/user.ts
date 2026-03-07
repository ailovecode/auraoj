export type UserRole = 'student' | 'teacher' | 'admin'

export interface UserInfo {
  id?: number
  username?: string
  avatar?: string | null
  gender?: number
  phone?: string
  email?: string
  school?: string | null
  signature?: string | null
  role?: UserRole
  level?: number
  score?: number
  status?: number
  loginTime?: string
}

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResponse {
  code: number
  data: UserInfo & { token: string }
  message: string
}

export interface AdminUserInfo extends UserInfo {
  gmtCreate?: string
  gmtModified?: string
}

export interface UserListData {
  pageNum: number
  pageSize: number
  total: number
  totalPages: number
  list: AdminUserInfo[]
  hasPrevious: boolean
  hasNext: boolean
}

export interface GetAllUsersRes {
  code: number
  data: UserListData
  message: string
}

export interface UpdateCurrentUser_1Params {
  username?: string
  phone?: string
  email?: string
  password?: string
  confirmPassword?: string
  avatar?: string
  gender?: number
  school?: string
  signature?: string
}

export interface UpdateCurrentUser_1Res {
  code: number
  data: Record<string, unknown>
  message: string
}

export interface DeleteUserRes {
  code: number
  data: Record<string, unknown>
  message: string
}
