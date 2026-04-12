package com.zhy.auraojbackend.model.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Description 单测试点调试结果 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseDebugResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 运行状态：Accepted, Wrong Answer, Time Limit Exceeded, Memory Limit Exceeded, Runtime Error, Compile Error
     */
    private String status;

    /**
     * 程序输出内容
     */
    private String output;

    /**
     * 标准错误输出
     */
    private String stderr;

    /**
     * 编译错误信息（如果编译失败）
     */
    private String compileError;

    /**
     * 实际耗时（纳秒）
     */
    private Long timeUsed;

    /**
     * 实际内存消耗（字节）
     */
    private Long memoryUsed;

    /**
     * 是否与期望输出一致（仅在提供了 expectedOutput 时有效）
     */
    private Boolean isCorrect;

    /**
     * 错误消息
     */
    private String errorMessage;
}
