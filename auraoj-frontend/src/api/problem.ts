import request from '@/utils/request'
import type { QueryAllProblemRes, ProblemAddRequest, ProblemAddRes, ProblemSearchRequest, GetProblemRes, DeleteProblemRes, UpdateProblemRequest } from '@/types/problem'

export function queryAllProblems(
  pageNum: number,
  pageSize: number,
  sortField: string = '',
  sortOrder: string = ''
): Promise<QueryAllProblemRes> {
  return request.get(`/problem/list?pageNum=${pageNum}&pageSize=${pageSize}&sortField=${sortField}&sortOrder=${sortOrder}`)
}

export function addProblem(params: ProblemAddRequest): Promise<ProblemAddRes> {
  return request.post('/problem/add', params)
}

export function updateProblem(params: UpdateProblemRequest): Promise<GetProblemRes> {
  return request.post('/problem/update', params)
}

export function searchProblem(
  pageNum: number,
  pageSize: number,
  params: ProblemSearchRequest
): Promise<QueryAllProblemRes> {
  return request.post(`/problem/search?pageNum=${pageNum}&pageSize=${pageSize}`, params)
}

export function getProblemDetail(problemId: number): Promise<GetProblemRes> {
  return request.get(`/problem/get/${problemId}`);
}

export function deleteProblem(problemId: number): Promise<DeleteProblemRes> {
  return request.delete(`/problem/delete/${problemId}`);
}
