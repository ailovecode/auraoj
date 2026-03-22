package com.zhy.auraojbackend.model.dto.problem.request;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import com.zhy.auraojbackend.model.enums.DifficultyLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 更新题目请求参数
 * @author zhy
 * @Date 2026/3/22
 */
@Data
public class ProblemUpdateRequest implements AbstractCheckRequest {

    @Schema(description = "题目 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "题目标题")
    private String title;

    @Schema(description = "时间限制 (毫秒)", example = "1000")
    private Integer timeLimit;

    @Schema(description = "内存限制 (MB)", example = "128")
    private Integer memoryLimit;

    @Schema(description = "题目描述")
    private String description;

    @Schema(description = "输入描述")
    private String inputDesc;

    @Schema(description = "输出描述")
    private String outputDesc;

    @Schema(description = "数据范围说明")
    private String dataScope;

    @Schema(description = "输入样例")
    private String sampleInput;

    @Schema(description = "输出样例")
    private String sampleOutput;

    @Schema(description = "难度等级")
    private DifficultyLevelEnum difficulty;

    @Schema(description = "是否公开")
    private Integer status;

    @Schema(description = "题目标签 ID 列表")
    private List<Long> tagIds;

    @Override
    public void check() {
        if (id == null) {
            throw new IllegalArgumentException("题目 ID 不能为空");
        }
        if (timeLimit != null && timeLimit <= 0) {
            throw new IllegalArgumentException("时间限制必须大于 0");
        }
        if (memoryLimit != null && memoryLimit <= 0) {
            throw new IllegalArgumentException("内存限制必须大于 0");
        }
        if (title != null && StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("题目标题不能为空");
        }
        if (description != null && StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("题目描述不能为空");
        }
        if (inputDesc != null && StringUtils.isBlank(inputDesc)) {
            throw new IllegalArgumentException("输入描述不能为空");
        }
        if (outputDesc != null && StringUtils.isBlank(outputDesc)) {
            throw new IllegalArgumentException("输出描述不能为空");
        }
    }
}
