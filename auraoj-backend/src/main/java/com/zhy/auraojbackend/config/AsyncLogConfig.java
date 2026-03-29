package com.zhy.auraojbackend.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 异步日志配置类
 * 注意：Logback 的异步配置通过 logback-spring.xml 完成
 * @author zhy
 * @Date 2026/3/29
 */
@Configuration
public class AsyncLogConfig {

    /**
     * 初始化异步日志配置
     * 此方法保留作为扩展点
     */
    @PostConstruct
    public void init() {
        // 异步日志配置已通过 logback-spring.xml 完成
        // 可以在这里添加动态配置逻辑
    }
}
