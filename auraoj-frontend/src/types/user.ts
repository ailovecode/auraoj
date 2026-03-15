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

// 基本用户信息返回参数
export interface BasicUserInfoRes {
  code: number
  data: UserInfo
  message: string
}

// 用户登出返回参数
export interface UserLogoutRes {
  code: number
  data: boolean
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
export interface UserRegisterRes {
  code: number
  data: number
  message: string
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
export interface GetAllUsersRes {
  code: number
  data: UserListData
  message: string
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

// 删除用户返回参数
export interface DeleteUserRes {
  code: number
  data: boolean
  message: string
}
