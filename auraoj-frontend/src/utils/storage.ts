/**
 * 本地存储工具类（增强版）
 * 提供类型安全的 localStorage 封装，支持过期时间管理
 */
class Storage {
  private prefix: string
  private readonly DEFAULT_EXPIRE = 7 * 24 * 60 * 60 * 1000 // 默认7天过期

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
   * 设置数据（支持过期时间）
   * @param key 键名
   * @param value 值（支持对象、字符串、数字等）
   * @param expireIn 过期时间（毫秒），默认7天
   */
  set<T>(key: string, value: T, expireIn?: number): void {
    try {
      const data = {
        value,
        expireAt: expireIn ? Date.now() + expireIn : Date.now() + this.DEFAULT_EXPIRE
      }
      const serialized = JSON.stringify(data)
      localStorage.setItem(this.getKey(key), serialized)
    } catch (error) {
      console.error(`存储失败 [${key}]:`, error)
    }
  }

  /**
   * 获取数据（自动检查过期）
   * @param key 键名
   * @param defaultValue 默认值
   * @returns 存储的值或默认值（过期或不存在返回 null）
   */
  get<T>(key: string, defaultValue?: T): T | null {
    try {
      const item = localStorage.getItem(this.getKey(key))
      if (item === null) {
        return defaultValue ?? null
      }

      const data = JSON.parse(item)

      // 检查是否过期
      if (data.expireAt && Date.now() > data.expireAt) {
        this.remove(key)
        return defaultValue ?? null
      }

      return data.value ?? defaultValue ?? null
    } catch (error) {
      console.error(`读取失败 [${key}]:`, error)
      return defaultValue ?? null
    }
  }

  /**
   * 获取数据（不过期，用于 token 等永久存储）
   */
  getNoExpire<T>(key: string, defaultValue?: T): T | null {
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
   * 设置数据（不过期）
   */
  setNoExpire<T>(key: string, value: T): void {
    try {
      const serialized = JSON.stringify(value)
      localStorage.setItem(this.getKey(key), serialized)
    } catch (error) {
      console.error(`存储失败 [${key}]:`, error)
    }
  }

  /**
   * 删除数据
   */
  remove(key: string): void {
    localStorage.removeItem(this.getKey(key))
  }

  /**
   * 检查数据是否过期
   */
  isExpired(key: string): boolean {
    try {
      const item = localStorage.getItem(this.getKey(key))
      if (item === null) return true

      const data = JSON.parse(item)
      if (!data.expireAt) return false

      return Date.now() > data.expireAt
    } catch {
      return true
    }
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

  /**
   * 清理所有过期的数据
   */
  cleanExpired(): void {
    const keys = Object.keys(localStorage)
    keys.forEach(key => {
      if (key.startsWith(this.prefix)) {
        const item = localStorage.getItem(key)
        if (item) {
          try {
            const data = JSON.parse(item)
            if (data.expireAt && Date.now() > data.expireAt) {
              localStorage.removeItem(key)
            }
          } catch {
            // 忽略解析错误
          }
        }
      }
    })
  }
}

export const storage = new Storage()
