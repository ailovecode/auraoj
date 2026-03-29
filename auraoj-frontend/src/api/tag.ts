import request from '@/utils/request'
import type { AddTagReq, TagInfo } from '@/types/tagInfo'
import type { Result } from '@/types/common'

export function listAllTags(tagName?: string): Promise<Result<TagInfo[]>> {
  if (tagName) {
    return request.get(`/tag/list?tagName=${tagName}`)
  }
  return request.get('/tag/list')
}

export function addTag(params: AddTagReq): Promise<Result<number>> {
  return request.post('/tag/add', params)
}


