import request from '@/utils/request'
import type {
  LoginParams,
  LoginResponse,
  UserListData,
  UserInfo,
  UserRegisterParams,
  UpdateUserParams
} from '@/types/user'
import type { Result } from '@/types/common'

// ---------------------【用户接口】--------------------

// 用户登录接口
export function userLogin(params: LoginParams): Promise<LoginResponse> {
  return request.post('/user/login', params)
}

// 获取当前用户信息接口
export function getUserInfo(): Promise<Result<UserInfo>> {
  return request.get('/user/current')
}

// 用户登出接口
export function userLogout(): Promise<Result<boolean>> {
  return request.post('/user/logout')
}

// 用户注册接口
export function userRegister(params: UserRegisterParams): Promise<Result<number>> {
  return request.post('/user/register', params)
}

// 上传用户头像接口
export function uploadAvatar(userId: number, file: File): Promise<Result<UserInfo>> {
  const formData = new FormData()
  formData.append('userId', userId.toString())
  formData.append('file', file)

  return request.post('/user/updateUserAvatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 用户修改个人信息接口
export function updateUserUserInfo(params: UpdateUserParams): Promise<Result<UserInfo>> {
  return request.post('/user/update', params)
}

// ---------------------【管理员和教师接口】--------------------

// 更新指定用户信息接口
export function updateCurrentUser(
  userId: number,
  params: UpdateUserParams
): Promise<Result<UserInfo>> {
  return request.post(`/user/admin/${userId}/update`, params)
}

// 删除用户接口
export function deleteUser(userId: number): Promise<Result<boolean>> {
  return request.delete(`/user/admin/${userId}/deleteUser`)
}

// 获取所有用户接口
export function getAllUsers(pageNum: number, pageSize: number): Promise<Result<UserListData>> {
  return request.get(`/user/admin/list?pageNum=${pageNum}&pageSize=${pageSize}`)
}
