import request from '@/utils/request'
import type { LanguageInfo, LanguageAddRequest, LanguageUpdateRequest, LanguageListData } from '@/types/language'
import type { Result } from '@/types/common'

// ---------------------【代码语言管理接口】--------------------

// 新增代码语言
export function addLanguage(params: LanguageAddRequest): Promise<Result<number>> {
  return request.post('/language/add', params)
}

// 更新代码语言
export function updateLanguage(params: LanguageUpdateRequest): Promise<Result<LanguageInfo>> {
  return request.post('/language/update', params)
}

// 删除代码语言
export function deleteLanguage(id: number): Promise<Result<boolean>> {
  return request.delete(`/language/delete/${id}`)
}

// 查询所有代码语言
export function listAllLanguages(pageNum: number, pageSize: number): Promise<Result<LanguageListData>> {
  return request.post(`/language/list?pageNum=${pageNum}&pageSize=${pageSize}`)
}

// 获取单个语言详情
export function getLanguageDetail(id: number): Promise<Result<LanguageInfo>> {
  return request.get(`/language/detail/${id}`)
}
