import request from '@/utils/request'
import type { SubmissionRes, ShowSubmissionRequest } from '@/types/judge'
import type { Result } from '@/types/common'

export function getSubmissionInfo(params: ShowSubmissionRequest): Promise<Result<SubmissionRes>> {
  return request.post(`/submission/get`, params);
}
