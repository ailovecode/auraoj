package com.zhy.auraojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.auraojbackend.model.dto.PageRequest;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.problem.BaseProblemInfo;
import com.zhy.auraojbackend.model.dto.problem.request.ProblemAddRequest;
import com.zhy.auraojbackend.model.dto.problem.request.ProblemUpdateRequest;
import com.zhy.auraojbackend.model.dto.problem.request.SearchProblemsRequest;
import com.zhy.auraojbackend.model.dto.problem.response.QueryAllProblemResponse;
import com.zhy.auraojbackend.model.entity.Problem;

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
     * 更新题目
     * @param problemUpdateRequest 更新题目请求参数
     * @return 更新后的题目
     */
    Problem updateProblem(ProblemUpdateRequest problemUpdateRequest);

    /**
     * 查询所有题目（分页）
     * @param pageRequest 分页请求参数
     * @return 分页后的题目列表
     */
    PageResponse<QueryAllProblemResponse> queryAllProblems(PageRequest pageRequest);

    /**
     * 根据题目名称或题号搜索题目（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param searchProblemsRequest 搜索参数
     * @return 分页后的题目列表
     */
    PageResponse<QueryAllProblemResponse> searchProblems(Integer pageNum, Integer pageSize, SearchProblemsRequest searchProblemsRequest);

    /**
     * 根据题目 ID 查询题目详情
     * @param problemId 题目 ID
     * @return 题目详情
     */
    BaseProblemInfo getProblemById(Long problemId);

    boolean deleteProblem(Long problemId);
}
