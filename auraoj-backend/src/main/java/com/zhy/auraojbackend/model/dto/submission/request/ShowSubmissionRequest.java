package com.zhy.auraojbackend.model.dto.submission.request;

import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import lombok.Data;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/28
 */
@Data
public class ShowSubmissionRequest {

    /**
     * 页码，从 1 开始
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式：asc / desc
     */
    private String sortOrder = "desc";

    /**
     * 提交 ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 题目 ID
     */
    private Long problemId;

    /**
     * 比赛 ID
     */
    private Long contestId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private SubmissionStatusEnum status;

    /**
     * 提交模式
     */
    private Integer pattern;
}
