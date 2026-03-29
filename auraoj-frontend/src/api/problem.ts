import request from '@/utils/request'
import type { ProblemAddRequest, ProblemListData, ProblemSearchRequest, BaseProblemInfo, UpdateProblemRequest, SubmitRequest, SubmitResponse } from '@/types/problem'
import type { Result } from '@/types/common'

export function queryAllProblems(
  pageNum: number,
  pageSize: number,
  sortField: string = '',
  sortOrder: string = ''
): Promise<Result<ProblemListData>> {
  return request.get(`/problem/list?pageNum=${pageNum}&pageSize=${pageSize}&sortField=${sortField}&sortOrder=${sortOrder}`)
}

export function addProblem(params: ProblemAddRequest): Promise<Result<number>> {
  return request.post('/problem/add', params)
}

export function updateProblem(params: UpdateProblemRequest): Promise<Result<BaseProblemInfo>> {
  return request.post('/problem/update', params)
}

export function searchProblem(
  pageNum: number,
  pageSize: number,
  params: ProblemSearchRequest
): Promise<Result<ProblemListData>> {
  return request.post(`/problem/search?pageNum=${pageNum}&pageSize=${pageSize}`, params)
}

export function getProblemDetail(problemId: number): Promise<Result<BaseProblemInfo>> {
  return request.get(`/problem/get/${problemId}`);
}

export function deleteProblem(problemId: number): Promise<Result<boolean>> {
  return request.delete(`/problem/delete/${problemId}`);
}

export function submit(params: SubmitRequest): Promise<Result<SubmitResponse>> {
  return request.post(`/submission/submit`, params);
}

export function getProblemsByTagId(tagId: number): Promise<Result<ProblemListData>> {
  return request.get(`/problem/getListProblemsByTagId/${tagId}`)
}
