package com.zhy.auraojbackend.model.dto.tag.request;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增标签请求参数
 * @author zhy
 * @Date 2026/3/15
 */
@Data
public class TagAddRequest implements AbstractCheckRequest {

    /**
     * 标签名
     */
    @NotBlank(message = "标签名不能为空")
    @Schema(description = "标签名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 标签类别：来源 - 1，知识点 - 2
     */
    @NotNull(message = "标签类别不能为空")
    @Schema(description = "标签类别：来源 - 1，知识点 - 2", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer classification;

    @Override
    public void check() {
        // 校验分类
        if (classification != 1 && classification != 2) {
            throw new IllegalArgumentException("标签类别必须为 1（来源）或 2（知识点）");
        }
    }
}
