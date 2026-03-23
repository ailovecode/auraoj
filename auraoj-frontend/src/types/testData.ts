// 通用 Result 类型
export interface Result<T> {
  code: number;
  data: T;
  message: string;
}

// ProblemCaseFileResponse
export interface ProblemCaseFileResponse {
  id: number | null;
  problemId: number | null;
  inputFile: string | null;
  outputFile: string | null;
  inputFileSize: number | null;
  outputFileSize: number | null;
  status: number | null;
  gmtCreate: string | null;
  gmtModified: string | null;
}

// SingleProblemCaseUploadRequest
export interface SingleProblemCaseUploadRequest {
  problemId: number;
  inputContent: string;
  outputContent: string;
  fileName: string;
}

// DeleteProblemCaseFileRequest
export interface DeleteProblemCaseFileRequest {
  problemId: number;
  fileName: string;
}

// ProblemCaseDeleteResponse
export interface ProblemCaseDeleteResponse {
  deleted: boolean | null;
  deletedFileName: string | null;
  pairedFileName: string | null;
  reminder: string | null;
}

export interface RenameTestDataRequest {
  problemId: number
  oldFileName: string
  newFileName: string
}
