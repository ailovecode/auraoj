export interface TagInfo {
  id: number
  name: string
  classification: number
  creatorId: number
  gmtCreate: string
  gmtModified: string
}

export interface QueryTagRes {
  code: number
  data: TagInfo[]
  message: string
}
