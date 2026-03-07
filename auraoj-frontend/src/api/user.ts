import request from '@/utils/request'
import type {
  LoginParams,
  LoginResponse,
  GetAllUsersRes,
  UpdateCurrentUser_1Params,
  UpdateCurrentUser_1Res,
  DeleteUserRes
} from '@/types/user'

export interface UserLogoutRes {
  code: number
  data: Record<string, unknown>
  message: string
}

export function userLogin(params: LoginParams): Promise<LoginResponse> {
  return request.post('/user/login', params)
}

export function getUserInfo(): Promise<LoginResponse> {
  return request.get('/user/current')
}

export function userLogout(): Promise<UserLogoutRes> {
  return request.post('/user/logout')
}

export function getAllUsers(pageNum: number, pageSize: number): Promise<GetAllUsersRes> {
  return request.get(`/user/admin/list?pageNum=${pageNum}&pageSize=${pageSize}`)
}

export function updateCurrentUser_1(
  userId: number,
  params: UpdateCurrentUser_1Params
): Promise<UpdateCurrentUser_1Res> {
  return request.post(`/user/admin/${userId}/update`, params)
}

export function deleteUser(userId: number): Promise<DeleteUserRes> {
  return request.delete(`/user/admin/${userId}/deleteUser`)
}
