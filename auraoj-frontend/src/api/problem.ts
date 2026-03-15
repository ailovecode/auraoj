import request from '@/utils/request'
import type { QueryAllProblemRes } from '@/types/problem'

export function queryAllProblems(
  pageNum: number,
  pageSize: number,
  sortField: string = '',
  sortOrder: string = ''
): Promise<QueryAllProblemRes> {
  return request.get(`/problem/list?pageNum=${pageNum}&pageSize=${pageSize}&sortField=${sortField}&sortOrder=${sortOrder}`)
}
