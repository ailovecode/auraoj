package com.zhy.auraojbackend.model.dto.submission.response;

import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import lombok.Data;

import java.util.Date;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/25
 */
@Data
public class SubmitResponse {

    /**
     * Submission id
     */
    private Long id;

    /**
     * User id
     */
    private Long userId;

    /**
     * Problem id
     */
    private Long problemId;

    /**
     * Contest id
     */
    private Long contestId;

    /**
     * Language
     */
    private String language;

    /**
     * Submission status
     */
    private SubmissionStatusEnum status;

    /**
     * Submit pattern
     */
    private Integer pattern;

    /**
     * Create time
     */
    private Date gmtCreate;
}