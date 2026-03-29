export interface TagInfo {
  id: number
  name: string
  classification: number
  creatorId: number
  gmtCreate: string
  gmtModified: string
}

// 添加请求参数
export interface AddTagReq {
  name: string
  // 标签分类，1：来源，2：知识点
  classification: number
}
