import type { Result } from '@/types/common'
import type { DeleteProblemCaseFileRequest, ProblemCaseDeleteResponse, ProblemCaseFileResponse, RenameTestDataRequest, SingleProblemCaseUploadRequest } from '@/types/testData'
import request from '@/utils/request'

// 获取测试数据列表
export function getTestDataList(problemId: number): Promise<Result<ProblemCaseFileResponse[]>> {
  return request.get(`/case/list/${problemId}`)
}

// 删除测试数据
export function deleteTestData(params: DeleteProblemCaseFileRequest): Promise<Result<ProblemCaseDeleteResponse>> {
  return request.delete(`/case/delete`, { data:params })
}

// 重命名测试数据
export function renameTestData(params: RenameTestDataRequest): Promise<Result<ProblemCaseFileResponse>> {
  return request.post(`/case/rename`, params)
}

// 创建测试数据
export function createTestData(params: SingleProblemCaseUploadRequest): Promise<Result<ProblemCaseFileResponse>> {
  return request.post('/case/upload/single', params)
}

// 上传测试数据文件
export function uploadTestDataFile(problemId: number, file: File): Promise<Result<ProblemCaseFileResponse>> {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('problemId', problemId.toString())
  return request.post('/case/upload/zip', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
