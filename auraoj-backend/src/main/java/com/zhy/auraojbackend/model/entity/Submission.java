package com.zhy.auraojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import lombok.Data;

import java.util.Date;

/**
 * 用户代码提交记录表
 * @TableName submission
 */
@TableName(value ="submission")
@Data
public class Submission {
    /**
     * 提交记录主键
     */
    @TableId
    private Long id;

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

    /**
     * 提交创建时间
     */
    private Date gmtCreate;

    /**
     * 状态更新时间
     */
    private Date gmtModified;
}