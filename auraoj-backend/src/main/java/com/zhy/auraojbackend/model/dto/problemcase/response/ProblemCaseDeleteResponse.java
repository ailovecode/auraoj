package com.zhy.auraojbackend.model.dto.problemcase.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhy
 */
@Data
@Builder
@Schema(description = "删除测试数据文件响应")
public class ProblemCaseDeleteResponse {

    @Schema(description = "是否删除成功")
    private Boolean deleted;

    @Schema(description = "已删除文件名")
    private String deletedFileName;

    @Schema(description = "关联文件名")
    private String pairedFileName;

    @Schema(description = "提醒信息")
    private String reminder;
}