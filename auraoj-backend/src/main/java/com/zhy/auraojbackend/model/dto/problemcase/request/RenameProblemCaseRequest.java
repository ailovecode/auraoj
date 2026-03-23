package com.zhy.auraojbackend.model.dto.problemcase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 重命名测试数据文件请求
 * @author zhy
 */
@Data
@Schema(description = "重命名测试数据文件请求")
public class RenameProblemCaseRequest {

    @Schema(description = "题目 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long problemId;

    @Schema(description = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.in")
    private String oldFileName;

    @Schema(description = "新文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "test.in")
    private String newFileName;
}
