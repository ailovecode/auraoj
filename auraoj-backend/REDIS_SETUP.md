# Redis 集成说明

## 1. 安装 Redis

### Windows 环境
1. 访问 GitHub 下载：https://github.com/microsoftarchive/redis/releases
2. 下载 `Redis-x64-3.0.504.msi` 或其他版本
3. 运行安装程序，按照提示完成安装
4. 验证安装：打开命令行，输入 `redis-cli --version`

或者使用 Docker：
```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

### Linux 环境
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install redis-server

# CentOS/RHEL
sudo yum install redis

# 启动 Redis
sudo systemctl start redis
sudo systemctl enable redis
```

### macOS 环境
```bash
brew install redis
brew services start redis
```

## 2. 配置环境变量

复制 `.env.example` 文件为 `.env`（或在系统环境变量中配置）：

```bash
# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=0
```

如果有密码，设置 `REDIS_PASSWORD`；如果没有密码，留空即可。

## 3. 验证 Redis 连接

```bash
redis-cli
PING
# 应该返回 PONG
```

## 4. 项目中的 Redis 使用

### 已集成功能

#### 4.1 提交频率限制
在 `SubmissionServiceImpl` 中实现了基于 Redis 的用户提交频率限制：
- 每个用户在 60 秒内最多提交 5 次
- 使用 Redis 的原子操作保证并发安全
- Key 格式：`submission:frequency:{userId}`

#### 4.2 Redis 工具类
提供了 `RedisUtils` 工具类，包含常用操作：
- `set(key, value)` - 设置缓存
- `set(key, value, timeout, unit)` - 设置带过期时间的缓存
- `get(key)` - 获取缓存
- `delete(key)` - 删除缓存
- `hasKey(key)` - 判断 key 是否存在
- `increment(key)` / `increment(key, delta)` - 自增操作
- `decrement(key)` - 自减操作

使用示例：
```java
@Resource
private RedisUtils redisUtils;

// 设置缓存
redisUtils.set("user:1", userInfo, 3600, TimeUnit.SECONDS);

// 获取缓存
Object obj = redisUtils.get("user:1");

// 自增操作
Long count = redisUtils.increment("submission:frequency:1");
```

### RedisTemplate 使用

也可以直接使用 `RedisTemplate`：
```java
@Resource
private RedisTemplate<String, Object> redisTemplate;

// 设置字符串
redisTemplate.opsForValue().set("key", "value");

// 设置对象（自动序列化）
redisTemplate.opsForValue().set("user:1", userInfo, 1, TimeUnit.HOURS);

// 获取对象
Object obj = redisTemplate.opsForValue().get("user:1");
```

## 5. 注意事项

1. **生产环境配置**：
   - 务必设置 Redis 密码
   - 配置合适的内存策略和最大内存
   - 启用持久化（RDB/AOF）

2. **连接池配置**：
   已在 `application.yaml` 中配置：
   ```yaml
   lettuce:
     pool:
       max-active: 8    # 最大连接数
       max-idle: 8      # 最大空闲连接数
       min-idle: 2      # 最小空闲连接数
       max-wait: -1ms   # 获取连接最大等待时间（-1 表示无限等待）
   ```

3. **性能优化**：
   - 合理设置 Key 的过期时间，避免内存占用
   - 使用批量操作减少网络开销
   - 对于热点数据，考虑本地缓存 + Redis 的多级缓存策略

## 6. 常见问题

### Q: Redis 连接失败？
A: 检查以下几点：
- Redis 服务是否启动
- 端口是否正确（默认 6379）
- 防火墙是否阻止连接
- 环境变量配置是否正确

### Q: 如何查看 Redis 中的数据？
A: 使用 `redis-cli` 命令：
```bash
redis-cli
KEYS *                    # 查看所有 key
GET submission:frequency:1  # 查看指定 key 的值
TTL submission:frequency:1  # 查看 key 的剩余过期时间
DEL submission:frequency:1  # 删除 key
```

### Q: 如何监控 Redis 状态？
A: 使用以下命令：
```bash
redis-cli INFO          # 查看 Redis 信息
redis-cli MONITOR       # 实时监控所有命令
redis-cli SLOWLOG GET   # 查看慢查询日志
```
