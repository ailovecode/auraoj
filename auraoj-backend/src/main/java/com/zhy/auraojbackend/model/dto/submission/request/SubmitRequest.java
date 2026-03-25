package com.zhy.auraojbackend.model.dto.submission.request;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/25
 */
@Data
public class SubmitRequest implements AbstractCheckRequest {

    /**
     * 外键：提交用户ID
     */
    private Long userId;

    /**
     * 外键：题目ID
     */
    private Long problemId;

    /**
     * 外键：比赛ID(非比赛则为null)
     */
    private Long contestId;

    /**
     * 用户提交的源代码
     */
    private String code;

    /**
     * 编程语言：Java/C++/Python等
     */
    private String language;

    /**
     * 判题状态：PENDING/AC/WA/CE等
     */
    private SubmissionStatusEnum status;

    /**
     * AI生成的错误分析文本
     */
    private String aiAnalyse;

    /**
     * 判题机返回的详细结果JSON
     */
    private String judgeSummary;

    /**
     * 第一个错误样例的详细信息
     */
    private String firstErrorCase;

    /**
     * 编译器输出日志
     */
    private String compileLog;

    /**
     * 提交模式：1练习 / 2比赛
     */
    private Integer pattern;

    @Override
    public void check() {
        // 校验参数非空
        if (userId == null || problemId == null || pattern == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "用户 ID、题目 ID、提交模式不能为空");
        }

        // 校验源代码非空
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "源代码不能为空");
        }

        // 校验编程语言非空
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "编程语言不能为空");
        }

        // 校验代码长度（防止过长代码）
        if (code.length() > 100000) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "代码长度不能超过 100000 字符");
        }

        // 校验语言长度
        if (language.length() > 50) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "编程语言名称长度不能超过 50 字符");
        }

        // 校验提交模式（1:练习，2:比赛）
        if (pattern != 1 && pattern != 2) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "提交模式必须为 1(练习) 或 2(比赛)");
        }
    }
}
