import request from '@/utils/request'
import type { QueryTagRes, AddTagReq, AddTagRes } from '@/types/tagInfo'

export function listAllTags(tagName?: string): Promise<QueryTagRes> {
  if (tagName) {
    return request.get(`/tag/list?tagName=${tagName}`)
  }
  return request.get('/tag/list')
}

export function addTag(params: AddTagReq): Promise<AddTagRes> {
  return request.post('/tag/add', params)
}
