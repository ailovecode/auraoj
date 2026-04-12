package com.zhy.auraojbackend.model.dto.submission.request;

import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @version 1.0
 * @Author zhy
 * @Description 在线调试请求 DTO
 *              用于单测试点即时调试，同步返回执行结果
 */
@Data
public class TestCaseDebugRequest implements AbstractCheckRequest {

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

    @Override
    public void check() {
        // 校验源代码非空
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "源代码不能为空");
        }

        // 校验编程语言非空
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "编程语言不能为空");
        }

        // 校验测试输入非空
        if (StringUtils.isBlank(testCaseInput)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "测试输入不能为空");
        }

        // 校验代码长度（防止过长代码）
        if (code.length() > 100000) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "代码长度不能超过 100000 字符");
        }

        // 校验语言长度
        if (language.length() > 50) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "编程语言名称长度不能超过 50 字符");
        }

        // 校验时间限制
        if (maxCpuTime != null && (maxCpuTime <= 0 || maxCpuTime > 30000)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "CPU 时间限制必须在 1-30000 毫秒之间");
        }

        // 校验内存限制
        if (maxMemory != null && (maxMemory <= 0 || maxMemory > 1024)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "内存限制必须在 1-1024 MB 之间");
        }
    }
}
