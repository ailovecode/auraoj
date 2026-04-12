package com.zhy.auraojbackend.model.dto.language.request;

import com.zhy.auraojbackend.model.dto.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询代码语言列表请求参数
 * @author zhy
 * @Date 2026/4/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LanguageQueryRequest extends PageRequest {

    @Schema(description = "语言名称（模糊查询）", example = "Java")
    private String name;
}
