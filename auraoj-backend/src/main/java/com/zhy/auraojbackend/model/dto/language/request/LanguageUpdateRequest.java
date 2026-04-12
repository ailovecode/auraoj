package com.zhy.auraojbackend.model.dto.language.request;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 更新代码语言请求参数
 * @author zhy
 * @Date 2026/4/12
 */
@Data
public class LanguageUpdateRequest implements AbstractCheckRequest {

    @NotNull(message = "语言 ID 不能为空")
    @Schema(description = "语言 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "语言名称", example = "Java")
    private String name;

    @Schema(description = "Monaco 编辑器识别的语言名称", example = "java")
    private String monacoName;

    @Schema(description = "代码模板", example = "public class Main {\n    public static void main(String[] args) {\n        \n    }\n}")
    private String codeTemplate;

    @Schema(description = "语言描述", example = "Java 编程语言")
    private String description;

    @Override
    public void check() {
        if (id == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "语言 ID 不能为空");
        }
    }
}
