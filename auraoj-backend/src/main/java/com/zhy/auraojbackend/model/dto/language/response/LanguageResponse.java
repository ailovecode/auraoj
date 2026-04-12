package com.zhy.auraojbackend.model.dto.language.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 代码语言响应信息
 * @author zhy
 * @Date 2026/4/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "语言 ID", example = "1")
    private Long id;

    @Schema(description = "语言名称", example = "Java")
    private String name;

    @Schema(description = "Monaco 编辑器识别的语言名称", example = "java")
    private String monacoName;

    @Schema(description = "代码模板", example = "public class Main {\n    public static void main(String[] args) {\n        \n    }\n}")
    private String codeTemplate;

    @Schema(description = "语言描述", example = "Java 编程语言")
    private String description;

    @Schema(description = "创建时间", example = "2026-04-12 16:00:00")
    private Date gmtCreate;

    @Schema(description = "修改时间", example = "2026-04-12 16:00:00")
    private Date gmtModified;
}
