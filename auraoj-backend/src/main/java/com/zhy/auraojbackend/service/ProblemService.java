package com.zhy.auraojbackend.service;

import com.zhy.auraojbackend.model.dto.problem.ProblemAddRequest;
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
    
}
