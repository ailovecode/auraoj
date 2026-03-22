package com.zhy.auraojbackend.model.dto.problem.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhy.auraojbackend.model.dto.problem.BaseProblemInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryAllProblemResponse extends BaseProblemInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目状态：1公开, 2私有, 3比赛中
     */
    private Integer status;

    /**
     * 创建者id
     */
    private Long creatorId;

    /**
     * 判题类型：default(普通) / spj(特判)
     */
    private String judgeType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gmtCreate;

    /**
     * 最后修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gmtModified;
}
