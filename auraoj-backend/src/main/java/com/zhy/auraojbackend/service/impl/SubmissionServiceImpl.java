package com.zhy.auraojbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.mapper.SubmissionMapper;
import com.zhy.auraojbackend.model.dto.submission.request.SubmitRequest;
import com.zhy.auraojbackend.model.dto.submission.response.SubmitResponse;
import com.zhy.auraojbackend.model.entity.Problem;
import com.zhy.auraojbackend.model.entity.Submission;
import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import com.zhy.auraojbackend.service.ProblemService;
import com.zhy.auraojbackend.service.SubmissionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author zhy
* @description Service implementation for submission records.
* @createDate 2026-03-25 21:28:04
*/
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission>
    implements SubmissionService {

    @Resource
    private ProblemService problemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitResponse submit(SubmitRequest submitRequest) {
        ThrowUtils.throwIf(submitRequest == null, ErrorCode.BAD_PARAMS, "submit request is null");

        try {
            submitRequest.check();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, e.getMessage());
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!currentUserId.equals(submitRequest.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "cannot submit for another user");
        }

        if (submitRequest.getPattern() == 2 && submitRequest.getContestId() == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "contestId is required in contest mode");
        }

        Problem problem = problemService.getById(submitRequest.getProblemId());
        ThrowUtils.throwIf(problem == null, ErrorCode.RESOURCE_NO_PROBLEM, "problem not found");

        Submission submission = new Submission();
        submission.setUserId(currentUserId);
        submission.setProblemId(submitRequest.getProblemId());
        submission.setContestId(submitRequest.getContestId());
        submission.setCode(submitRequest.getCode());
        submission.setLanguage(submitRequest.getLanguage());
        submission.setStatus(submitRequest.getStatus() != null
                ? submitRequest.getStatus()
                : SubmissionStatusEnum.PENDING);
        submission.setAiAnalyse(submitRequest.getAiAnalyse());
        submission.setJudgeSummary(submitRequest.getJudgeSummary());
        submission.setFirstErrorCase(submitRequest.getFirstErrorCase());
        submission.setCompileLog(submitRequest.getCompileLog());
        submission.setPattern(submitRequest.getPattern());

        boolean saveResult = this.save(submission);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "failed to create submission");

        Problem updateProblem = new Problem();
        updateProblem.setId(problem.getId());
        updateProblem.setSubmitNum((problem.getSubmitNum() == null ? 0 : problem.getSubmitNum()) + 1);
        boolean updateProblemResult = problemService.updateById(updateProblem);
        ThrowUtils.throwIf(!updateProblemResult, ErrorCode.SYSTEM_ERROR, "failed to update submit count");

        Submission savedSubmission = this.getById(submission.getId());
        SubmitResponse submitResponse = new SubmitResponse();
        BeanUtils.copyProperties(savedSubmission != null ? savedSubmission : submission, submitResponse);
        return submitResponse;
    }
}