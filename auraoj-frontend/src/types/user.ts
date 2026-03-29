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
  gmtCreate?: string
  gmtModified?: string
}

// 登录参数
export interface LoginParams {
  username: string
  password: string
}
export interface LoginResponse {
  code: number
  data: UserInfo & { token: string } & { loginTime: string }
  message: string
}

// 注册接口参数
export interface UserRegisterParams {
  username?: string
  password?: string
  confirmPassword?: string
  phone?: string
  email?: string
  gender?: number
  school?: string
}

// 获取所有用户返回参数
export interface UserListData {
  pageNum: number
  pageSize: number
  total: number
  totalPages: number
  list: UserInfo[]
  hasPrevious: boolean
  hasNext: boolean
}
// 更新用户信息参数
export interface UpdateUserParams {
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

