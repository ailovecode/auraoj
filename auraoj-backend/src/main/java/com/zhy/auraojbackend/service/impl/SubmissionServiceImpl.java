package com.zhy.auraojbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.common.Constants;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.judge.JudgeDispatcher;
import com.zhy.auraojbackend.mapper.SubmissionMapper;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.submission.ShowSubmissionInfo;
import com.zhy.auraojbackend.model.dto.submission.request.ShowSubmissionRequest;
import com.zhy.auraojbackend.model.dto.submission.request.SubmitRequest;
import com.zhy.auraojbackend.model.dto.submission.response.SubmitResponse;
import com.zhy.auraojbackend.model.entity.Problem;
import com.zhy.auraojbackend.model.entity.Submission;
import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import com.zhy.auraojbackend.service.ProblemService;
import com.zhy.auraojbackend.service.SubmissionService;
import com.zhy.auraojbackend.service.UserInfoService;
import com.zhy.auraojbackend.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author zhy
* @description 提交记录的 Service 实现类。
* @createDate 2026-03-25 21:28:04
*/
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission>
    implements SubmissionService {

    @Resource
    private ProblemService problemService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    @Lazy
    private JudgeDispatcher judgeDispatcher;

    // 提交频率限制常量
    private static final int SUBMIT_TIME_LIMIT_SECONDS = 10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitResponse submit(SubmitRequest submitRequest) {
        ThrowUtils.throwIf(submitRequest == null, ErrorCode.BAD_PARAMS, "提交请求为空");

        try {
            submitRequest.check();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, e.getMessage());
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!currentUserId.equals(submitRequest.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无法为其他用户提交");
        }

        if (submitRequest.getPattern() == 2 && submitRequest.getContestId() == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "比赛模式下比赛 ID 不能为空");
        }
        
        // 通过 redis 限制用户提交频率
        String lockKey = Constants.Account.SUBMIT_NON_CONTEST_LOCK.getCode() + submitRequest.getUserId();

        if (!redisUtils.isWithInRateLimit(lockKey, SUBMIT_TIME_LIMIT_SECONDS)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "提交频率过高");
        }

        boolean isContestSubmission = submitRequest.getContestId() != null && submitRequest.getContestId() != 0;
        
        Problem problem = problemService.getById(submitRequest.getProblemId());
        ThrowUtils.throwIf(problem == null, ErrorCode.RESOURCE_NO_PROBLEM, "题目不存在");

        Submission submission = new Submission();
        submission.setUserId(currentUserId)
                .setProblemId(submitRequest.getProblemId())
                .setContestId(submitRequest.getContestId())
                .setCode(submitRequest.getCode())
                .setLanguage(submitRequest.getLanguage())
                .setStatus(SubmissionStatusEnum.PENDING)
                .setAiAnalyse(submitRequest.getAiAnalyse())
                .setJudgeSummary(submitRequest.getJudgeSummary())
                .setFirstErrorCase(submitRequest.getFirstErrorCase())
                .setCompileLog(submitRequest.getCompileLog())
                .setPattern(submitRequest.getPattern());

        boolean saveResult = this.save(submission);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "创建提交记录失败");

        Problem updateProblem = new Problem();
        updateProblem.setId(problem.getId());
        updateProblem.setSubmitNum((problem.getSubmitNum() == null ? 0 : problem.getSubmitNum()) + 1);
        boolean updateProblemResult = problemService.updateById(updateProblem);
        ThrowUtils.throwIf(!updateProblemResult, ErrorCode.SYSTEM_ERROR, "更新提交数量失败");

        Submission savedSubmission = this.getById(submission.getId());
        SubmitResponse submitResponse = new SubmitResponse();
        BeanUtils.copyProperties(savedSubmission != null ? savedSubmission : submission, submitResponse);
        // 异步处理判题任务
        judgeDispatcher.sendTask(savedSubmission.getId(),
                savedSubmission.getProblemId(),
                isContestSubmission);

        return submitResponse;
    }

    @Override
    public PageResponse<ShowSubmissionInfo> getSubmissionInfo(ShowSubmissionRequest showSubmissionRequest) {
        ThrowUtils.throwIf(showSubmissionRequest == null, ErrorCode.BAD_PARAMS, "showSubmissionRequest is null");

        int pageNum = showSubmissionRequest.getPageNum() == null ? 1 : showSubmissionRequest.getPageNum();
        int pageSize = showSubmissionRequest.getPageSize() == null ? 10 : showSubmissionRequest.getPageSize();
        ThrowUtils.throwIf(pageNum <= 0 || pageSize <= 0, ErrorCode.BAD_PARAMS, "page params are invalid");

        LambdaQueryWrapper<Submission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(showSubmissionRequest.getId() != null, Submission::getId, showSubmissionRequest.getId());
        queryWrapper.eq(showSubmissionRequest.getProblemId() != null, Submission::getProblemId, showSubmissionRequest.getProblemId());
        queryWrapper.eq(showSubmissionRequest.getContestId() != null, Submission::getContestId, showSubmissionRequest.getContestId());
        queryWrapper.eq(StrUtil.isNotBlank(showSubmissionRequest.getLanguage()), Submission::getLanguage, showSubmissionRequest.getLanguage());
        queryWrapper.eq(showSubmissionRequest.getStatus() != null, Submission::getStatus, showSubmissionRequest.getStatus());
        queryWrapper.eq(showSubmissionRequest.getPattern() != null, Submission::getPattern, showSubmissionRequest.getPattern());

        List<UserInfo> matchedUsers = Collections.emptyList();
        if (StrUtil.isNotBlank(showSubmissionRequest.getUsername())) {
            LambdaQueryWrapper<UserInfo> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.like(UserInfo::getUsername, showSubmissionRequest.getUsername());
            matchedUsers = userInfoService.list(userQueryWrapper);
            if (CollUtil.isEmpty(matchedUsers)) {
                return new PageResponse<>(pageNum, pageSize, 0L, Collections.emptyList());
            }
            List<Long> userIds = matchedUsers.stream()
                    .map(UserInfo::getId)
                    .toList();
            queryWrapper.in(Submission::getUserId, userIds);
        }

        applyOrder(queryWrapper, showSubmissionRequest.getSortField(), showSubmissionRequest.getSortOrder());

        Page<Submission> page = new Page<>(pageNum, pageSize);
        Page<Submission> resultPage = this.page(page, queryWrapper);

        Map<Long, UserInfo> userInfoMap = buildUserInfoMap(resultPage.getRecords(), matchedUsers);
        List<ShowSubmissionInfo> resultList = resultPage.getRecords().stream()
                .map(submission -> convertToShowSubmissionInfo(submission, userInfoMap.get(submission.getUserId())))
                .toList();

        PageResponse<ShowSubmissionInfo> response = new PageResponse<>();
        response.setPageNum(Math.toIntExact(resultPage.getCurrent()));
        response.setPageSize((int) resultPage.getSize());
        response.setTotal(resultPage.getTotal());
        response.setTotalPages((int) resultPage.getPages());
        response.setList(resultList);
        response.setHasPrevious(resultPage.getCurrent() > 1);
        response.setHasNext(resultPage.getCurrent() < resultPage.getPages());
        return response;
    }

    private void fillJudgeInfo(String judgeSummary, ShowSubmissionInfo result) {
        if (StrUtil.isBlank(judgeSummary) || !JSONUtil.isTypeJSON(judgeSummary)) {
            return;
        }
        try {
            JSONObject judgeJson = JSONUtil.parseObj(judgeSummary);
            result.setTime(extractInteger(judgeJson, "timeLimit", "timeUsed", "timeCost", "maxTime", "runTime"));
            result.setMemory(extractInteger(judgeJson, "memoryLimit", "memoryUsed", "memoryCost", "maxMemory", "runMemory"));
        } catch (Exception ignored) {
            // Ignore malformed judge summary to keep detail query available.
        }
    }

    private Integer extractInteger(JSONObject jsonObject, String... keys) {
        for (String key : keys) {
            Object value = jsonObject.getByPath(key);
            Integer number = toInteger(value);
            if (number != null) {
                return number;
            }
        }
        for (Object value : jsonObject.values()) {
            Integer number = extractIntegerFromValue(value, keys);
            if (number != null) {
                return number;
            }
        }
        return null;
    }

    private Integer extractIntegerFromValue(Object value, String... keys) {
        if (value instanceof JSONObject nestedObject) {
            return extractInteger(nestedObject, keys);
        }
        if (value instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                Integer number = extractIntegerFromValue(item, keys);
                if (number != null) {
                    return number;
                }
            }
        }
        return null;
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof CharSequence charSequence) {
            String text = charSequence.toString().trim();
            if (StrUtil.isNumeric(text)) {
                return Integer.parseInt(text);
            }
        }
        return null;
    }

    private void applyOrder(LambdaQueryWrapper<Submission> queryWrapper, String sortField, String sortOrder) {
        boolean asc = "asc".equalsIgnoreCase(sortOrder);
        if ("id".equalsIgnoreCase(sortField)) {
            if (asc) {
                queryWrapper.orderByAsc(Submission::getId);
            } else {
                queryWrapper.orderByDesc(Submission::getId);
            }
            return;
        }
        if ("problemId".equalsIgnoreCase(sortField)) {
            if (asc) {
                queryWrapper.orderByAsc(Submission::getProblemId);
            } else {
                queryWrapper.orderByDesc(Submission::getProblemId);
            }
            return;
        }
        queryWrapper.orderByDesc(Submission::getGmtCreate);
    }

    private Map<Long, UserInfo> buildUserInfoMap(List<Submission> submissions, List<UserInfo> matchedUsers) {
        Set<Long> userIds = submissions.stream()
                .map(Submission::getUserId)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UserInfo> userInfos;
        if (CollUtil.isNotEmpty(matchedUsers)) {
            userInfos = matchedUsers.stream()
                    .filter(userInfo -> userIds.contains(userInfo.getId()))
                    .toList();
        } else {
            LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(UserInfo::getId, userIds);
            userInfos = userInfoService.list(queryWrapper);
        }
        return userInfos.stream().collect(Collectors.toMap(UserInfo::getId, Function.identity(), (a, b) -> a));
    }

    private ShowSubmissionInfo convertToShowSubmissionInfo(Submission submission, UserInfo userInfo) {
        ShowSubmissionInfo result = new ShowSubmissionInfo();
        result.setId(submission.getId());
        result.setProblemId(submission.getProblemId());
        result.setLanguage(submission.getLanguage());
        result.setStatus(submission.getStatus());
        result.setAiAnalyse(submission.getAiAnalyse());
        result.setGmtCreate(submission.getGmtCreate());
        if (userInfo != null) {
            result.setUsername(userInfo.getUsername());
        }

        String code = submission.getCode();
        result.setCodeLength(code == null ? 0 : code.length());
        fillJudgeInfo(submission.getJudgeSummary(), result);
        return result;
    }
}
