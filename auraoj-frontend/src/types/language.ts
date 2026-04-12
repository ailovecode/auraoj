// 语言信息接口
export interface LanguageInfo {
  id?: number
  name: string
  monacoName: string
  codeTemplate?: string
  description?: string
  gmtCreate?: string
  gmtModified?: string
}

// 新增语言请求参数
export interface LanguageAddRequest {
  name: string
  monacoName: string
  codeTemplate?: string
  description?: string
}

// 更新语言请求参数
export interface LanguageUpdateRequest {
  id: number
  name?: string
  monacoName?: string
  codeTemplate?: string
  description?: string
}

// 语言列表响应数据
export interface LanguageListData {
  pageNum: number
  pageSize: number
  total: number
  totalPages: number
  list: LanguageInfo[]
  hasPrevious: boolean
  hasNext: boolean
}
