package com.zhy.auraojbackend.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * @author zhy
 * @Date 2026/3/29
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存（带过期时间）
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 判断 key 是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 自增操作
     * @param key 键
     * @return 自增后的值
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增操作（指定步长）
     * @param key 键
     * @param delta 步长
     * @return 自增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减操作
     * @param key 键
     * @return 自减后的值
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // ========== list 操作 ==========
    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将list左边放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean llPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从 list 中右边弹出一个元素
     * @param key
     * @return
     */
    public Object lrPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isWithInRateLimit(String key, long limitPeriod) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        long currentTime = System.currentTimeMillis();

        Boolean ifAbsent = ops.setIfAbsent(key, String.valueOf(currentTime + limitPeriod * 1000),
                limitPeriod, TimeUnit.SECONDS);

        if(Boolean.TRUE.equals(ifAbsent)) {
            // 尝试新添加了一个锁
            return true;
        } else {
            long expireAt = Long.parseLong(ops.get(key).toString());
            if (currentTime < expireAt) {
                return false;
            } else {
                // 锁已过期
                ops.set(key, String.valueOf(currentTime + limitPeriod * 1000), limitPeriod, TimeUnit.SECONDS);
                return true;
            }
        }
    }
}
