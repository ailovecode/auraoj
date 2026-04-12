package com.zhy.auraojbackend.model.dto.submission;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Description 单测试点调试请求 DTO
 *              用于在线调试功能，针对单个测试样例执行代码
 */
@Data
@Accessors(chain = true)
public class TestCaseDebugDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户提交的源代码
     */
    private String code;

    /**
     * 编程语言：Java/C++/Python等
     */
    private String language;

    /**
     * 测试输入内容
     */
    private String testCaseInput;

    /**
     * 期望输出内容（可选，用于比对）
     */
    private String expectedOutput;

    /**
     * CPU 时间限制（毫秒），默认 1000ms
     */
    private Long maxCpuTime = 1000L;

    /**
     * 内存限制（MB），默认 256MB
     */
    private Long maxMemory = 256L;
}
