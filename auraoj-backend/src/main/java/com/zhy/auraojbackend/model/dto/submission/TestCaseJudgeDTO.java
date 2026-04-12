package com.zhy.auraojbackend.model.dto.submission;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Description 在线调试判题请求 DTO
 *              用于发送给判题服务进行单测试点调试
 */
@Data
@Accessors(chain = true)
public class TestCaseJudgeDTO implements Serializable {
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
    private String input;

    /**
     * 期望输出内容（可选，用于比对）
     */
    private String output;

    /**
     * CPU 时间限制（毫秒）
     */
    private Long maxCpuTime;

    /**
     * 内存限制（MB）
     */
    private Long maxMemory;
}
