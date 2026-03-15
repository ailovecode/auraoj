/**
 * 本地存储工具类
 * 提供类型安全的 localStorage 封装
 */
class Storage {
  private prefix: string

  constructor(prefix: string = 'aura_') {
    this.prefix = prefix
  }

  /**
   * 生成带前缀的键名
   */
  private getKey(key: string): string {
    return `${this.prefix}${key}`
  }

  /**
   * 设置数据
   * @param key 键名
   * @param value 值（支持对象、字符串、数字等）
   */
  set<T>(key: string, value: T): void {
    try {
      const serialized = JSON.stringify(value)
      localStorage.setItem(this.getKey(key), serialized)
    } catch (error) {
      console.error(`存储失败 [${key}]:`, error)
    }
  }

  /**
   * 获取数据
   * @param key 键名
   * @param defaultValue 默认值
   * @returns 存储的值或默认值
   */
  get<T>(key: string, defaultValue?: T): T | null {
    try {
      const item = localStorage.getItem(this.getKey(key))
      if (item === null) {
        return defaultValue ?? null
      }
      return JSON.parse(item) as T
    } catch (error) {
      console.error(`读取失败 [${key}]:`, error)
      return defaultValue ?? null
    }
  }

  /**
   * 删除数据
   * @param key 键名
   */
  remove(key: string): void {
    localStorage.removeItem(this.getKey(key))
  }

  /**
   * 清空所有带前缀的数据
   */
  clear(): void {
    const keys = Object.keys(localStorage)
    keys.forEach(key => {
      if (key.startsWith(this.prefix)) {
        localStorage.removeItem(key)
      }
    })
  }
}

export const storage = new Storage()
