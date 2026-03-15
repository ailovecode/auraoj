package com.zhy.auraojbackend.service;

import com.zhy.auraojbackend.model.dto.PageRequest;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.problem.request.ProblemAddRequest;
import com.zhy.auraojbackend.model.dto.problem.response.QueryAllProblemResponse;
import com.zhy.auraojbackend.model.entity.Problem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zhy
* @description 针对表【problem(题目信息表)】的数据库操作 Service
* @createDate 2026-03-15 16:56:14
*/
public interface ProblemService extends IService<Problem> {

    /**
     * 新增题目
     * @param problemAddRequest 新增题目请求参数
     * @return 题目 ID
     */
    Long addProblem(ProblemAddRequest problemAddRequest);

    /**
     * 查询所有题目（分页）
     * @param pageRequest 分页请求参数
     * @return 分页后的题目列表
     */
    PageResponse<QueryAllProblemResponse> queryAllProblems(PageRequest pageRequest);
}
