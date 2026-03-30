package com.zhy.auraojbackend.model.dto.submission;

import com.zhy.auraojbackend.model.entity.Submission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
@Data
@Accessors(chain = true)
public class ToJudgeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  提交记录
     */
    private Submission submission;

    /**
     *  调用评测验证的token
     */
    private String token;
}
