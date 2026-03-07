package com.zhy.auraojbackend.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页响应数据
 *
 * @author zhy
 * @date 2026/3/7
 */
@Data
public class PageResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    public PageResponse() {
    }

    public PageResponse(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        // 计算总页数
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        // 判断是否有上一页和下一页
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < totalPages;
    }
}
