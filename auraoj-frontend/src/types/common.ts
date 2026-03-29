// 通用 Result 类型
export interface Result<T> {
  code: number;
  data: T;
  message: string;
}
