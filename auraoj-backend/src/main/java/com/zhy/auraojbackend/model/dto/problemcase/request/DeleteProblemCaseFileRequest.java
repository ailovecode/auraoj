package com.zhy.auraojbackend.model.dto.problemcase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhy
 */
@Data
@Schema(description = "删除测试数据文件请求")
public class DeleteProblemCaseFileRequest {

    @Schema(description = "题目 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long problemId;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.in")
    private String fileName;
}