package com.zhy.auraojbackend.model.dto.problemcase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhy
 */
@Data
@Schema(description = "单个测试数据上传请求")
public class SingleProblemCaseUploadRequest {

    @Schema(description = "题目 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long problemId;

    @Schema(description = "输入内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String inputContent;

    @Schema(description = "输出内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String outputContent;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileName;
}