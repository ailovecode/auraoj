package com.zhy.auraojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 题目测试样例表（存储文件路径或内容摘要）
 * @author zhy
 * @TableName problem_case
 */
@TableName(value ="public.problem_case")
@Data
public class ProblemCase {
    @TableId
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "题目 id")
    private Long problemId;

    @Schema(description = "输入文件")
    private String inputFile;

    @Schema(description = "输出文件")
    private String outputFile;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private Date gmtCreate;

    @Schema(description = "修改时间")
    private Date gmtModified;
}