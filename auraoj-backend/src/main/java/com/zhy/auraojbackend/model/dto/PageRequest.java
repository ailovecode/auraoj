package com.zhy.auraojbackend.model.dto;

import lombok.Data;

/**
 * 分页请求参数
 *
 * @author zhy
 * @date 2026/3/7
 */
@Data
public class PageRequest {

    /**
     * 页码，从 1 开始
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式：asc 或 desc
     */
    private String sortOrder = "desc";

    /**
     * 标签 ID
     */
    private Long tagId;
}
