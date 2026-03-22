package com.zhy.auraojbackend.model.dto.problem.request;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import com.zhy.auraojbackend.model.enums.DifficultyLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 新增题目请求参数
 * @author zhy
 * @Date 2026/3/15
 */
@Data
public class ProblemAddRequest implements AbstractCheckRequest {

    /**
     * 题目标题
     */
    @NotBlank(message = "题目标题不能为空")
    @Schema(description = "题目标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /**
     * 时间限制 (单位：ms)
     */
    @NotNull(message = "时间限制不能为空")
    @Schema(description = "时间限制 (毫秒)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Integer timeLimit;

    @NotNull(message = "题目状态不能为空")
    @Schema(description = "题目状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    /**
     * 内存限制 (单位：MB)
     */
    @NotNull(message = "内存限制不能为空")
    @Schema(description = "内存限制 (MB)", requiredMode = Schema.RequiredMode.REQUIRED, example = "128")
    private Integer memoryLimit;

    /**
     * 题目描述 (Markdown 格式)
     */
    @NotBlank(message = "题目描述不能为空")
    @Schema(description = "题目描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    /**
     * 输入描述
     */
    @NotBlank(message = "输入描述不能为空")
    @Schema(description = "输入描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String inputDesc;

    /**
     * 输出描述
     */
    @NotBlank(message = "输出描述不能为空")
    @Schema(description = "输出描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String outputDesc;

    /**
     * 数据范围
     */
    @Schema(description = "数据范围说明")
    private String dataScope;

    /**
     * 输入样例
     */
    @Schema(description = "输入样例")
    private String sampleInput;

    /**
     * 输出样例
     */
    @Schema(description = "输出样例")
    private String sampleOutput;

    /**
     * 难度等级
     */
    @NotNull(message = "难度等级不能为空")
    @Schema(description = "难度等级", requiredMode = Schema.RequiredMode.REQUIRED)
    private DifficultyLevelEnum difficulty;

    /**
     * 题目标签 (逗号分隔)
     */
    @Schema(description = "题目标签 (逗号分隔)")
    private List<Long> tagIds;

    @Override
    public void check() {
        // 校验时间限制
        if (timeLimit <= 0) {
            throw new IllegalArgumentException("时间限制必须大于 0");
        }
        
        // 校验内存限制
        if (memoryLimit <= 0) {
            throw new IllegalArgumentException("内存限制必须大于 0");
        }
        
        // 校验难度等级
        if (difficulty == null) {
            throw new IllegalArgumentException("难度等级不能为空");
        }
    }
}
