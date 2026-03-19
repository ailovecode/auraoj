package com.zhy.auraojbackend.model.dto.problem.request;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import lombok.Data;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/18
 */
@Data
public class SearchProblemsRequest implements AbstractCheckRequest {
    /**
     * 题目名称
     */
    private String title;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 难度
     */
    private String difficulty;

    @Override
    public void check() {
    }
}
