package com.zhy.auraojbackend.model.dto.problemcase.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author zhy
 */
@Data
@Schema(description = "测试数据文件信息")
public class ProblemCaseFileResponse {

    @Schema(description = "记录 ID")
    private Long id;

    @Schema(description = "题目 ID")
    private Long problemId;

    @Schema(description = "输入文件名")
    private String inputFile;

    @Schema(description = "输出文件名")
    private String outputFile;

    @Schema(description = "输入文件大小")
    private Long inputFileSize;

    @Schema(description = "输出文件大小")
    private Long outputFileSize;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;
}