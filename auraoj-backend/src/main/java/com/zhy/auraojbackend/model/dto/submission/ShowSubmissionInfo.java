package com.zhy.auraojbackend.model.dto.submission;

import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import lombok.Data;

import java.util.Date;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/25
 *
 * 展示非竞赛的提交记录
 */
@Data
public class ShowSubmissionInfo {

    /**
     * 提交ID
     */
    private Long id;

    /**
     * 外键：提交用户ID
     */
    private String username;

    /**
     * 时间
     */
    private Integer time;

    /**
     * 内存
     */
    private Integer memory;

    /**
     * 外键：题目ID
     */
    private Long problemId;

    /**
     * 编程语言：Java/C++/Python等
     */
    private String language;

    /**
     * 判题状态：PENDING/AC/WA/CE等
     */
    private SubmissionStatusEnum status;

    /**
     * 代码长度
     */
    private Integer codeLength;

    /**
     * AI生成的错误分析文本
     */
    private String aiAnalyse;

    /**
     * 提交创建时间
     */
    private Date gmtCreate;
}
