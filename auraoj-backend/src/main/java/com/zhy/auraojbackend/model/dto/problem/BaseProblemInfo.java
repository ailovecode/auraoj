package com.zhy.auraojbackend.model.dto.problem;

import com.zhy.auraojbackend.model.entity.TagInfo;
import com.zhy.auraojbackend.model.enums.DifficultyLevelEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/15
 */
@Data
public class BaseProblemInfo {
    /**
     * 题目主键ID
     */
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
     * 时间限制 (单位：ms)
     */
    private int timeLimit;

    /**
     * 内存限制 (单位：MB)
     */
    private int memoryLimit;

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
     * 样例输出
     */
    private String sampleOutput;

    /**
     * 样例输入
     */
    private String sampleInput;

    /**
     * 标签列表
     */
    List<TagInfo> tags;
}
