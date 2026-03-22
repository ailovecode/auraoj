package com.zhy.auraojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhy.auraojbackend.model.enums.DifficultyLevelEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 题目信息表
 * @author zhy
 * @TableName problem
 */
@TableName(value ="public.problem")
@Data
public class Problem {
    /**
     * 题目主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目描述 (Markdown 格式)
     */
    private String description;
    
    /**
     * 输入格式说明
     */
    private String inputDesc;
    
    /**
     * 输出格式说明
     */
    private String outputDesc;
    
    /**
     * 数据范围说明
     */
    private String dataScope;
    
    /**
     * 判题配置 {"timeLimit": 1000, "memoryLimit": 128}
     */
    private String judgeConfig;

    /**
     * 判题类型：default(普通) / spj(特判)
     */
    private String judgeType;

    /**
     * 难度等级：easy/medium/hard
     */
    private DifficultyLevelEnum difficulty;

    /**
     * 总提交次数
     */
    private Integer submitNum;

    /**
     * 总通过次数
     */
    private Integer acceptNum;

    /**
     * 通过率计算
     */
    private BigDecimal passRate;

    /**
     * 收藏数量
     */
    private Integer favourNum;

    /**
     * 题目状态：1公开, 2私有, 3比赛中
     */
    private Integer status;

    /**
     * 创建者id
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gmtCreate;

    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gmtModified;

    /**
     * 样例输出
     */
    private String sampleOutput;

    /**
     * 样例输入
     */
    private String sampleInput;
}