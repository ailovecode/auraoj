package com.zhy.auraojbackend.model.entity;

import lombok.Data;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/19
 */
@Data
public class JudgeConfig {
    /**
     * 时间限制，单位 ms
     */
    private int timeLimit;

    /**
     * 内存限制，单位 MB
     */
    private int memoryLimit;
}
