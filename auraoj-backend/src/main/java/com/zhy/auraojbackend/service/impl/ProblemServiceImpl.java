package com.zhy.auraojbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.mapper.ProblemMapper;
import com.zhy.auraojbackend.model.dto.PageRequest;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.problem.BaseProblemInfo;
import com.zhy.auraojbackend.model.dto.problem.request.ProblemAddRequest;
import com.zhy.auraojbackend.model.dto.problem.request.SearchProblemsRequest;
import com.zhy.auraojbackend.model.dto.problem.response.QueryAllProblemResponse;
import com.zhy.auraojbackend.model.entity.Problem;
import com.zhy.auraojbackend.model.entity.ProblemTagMap;
import com.zhy.auraojbackend.model.entity.TagInfo;
import com.zhy.auraojbackend.service.ProblemFileService;
import com.zhy.auraojbackend.service.ProblemService;
import com.zhy.auraojbackend.service.ProblemTagMapService;
import com.zhy.auraojbackend.service.TagService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author zhy
* @description 针对表【problem(题目信息表)】的数据库操作 Service 实现
* @createDate 2026-03-15 16:56:14
*/
@Slf4j
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem>
    implements ProblemService {

    @Resource
    private ProblemTagMapService problemTagMapService;
    @Resource
    private ProblemFileService problemFileService;
    @Resource
    private TagService tagService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProblem(ProblemAddRequest problemAddRequest) {
        // 1. 参数校验
        try {
            problemAddRequest.check();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, e.getMessage());
        }

        // 2. 构建 Problem 实体
        Problem problem = new Problem();
        problem.setTitle(problemAddRequest.getTitle());
        problem.setDescription(problemAddRequest.getDescription());
        problem.setInputDesc(problemAddRequest.getInputDesc());
        problem.setOutputDesc(problemAddRequest.getOutputDesc());
        problem.setDataScope(problemAddRequest.getDataScope());
        problem.setSampleInput(problemAddRequest.getSampleInput());
        problem.setSampleOutput(problemAddRequest.getSampleOutput());
        problem.setDifficulty(problemAddRequest.getDifficulty());
        problem.setCreatorId(StpUtil.getLoginIdAsLong());
        
        // 构建判题配置 JSON
        Map<String, Object> judgeConfigMap = new HashMap<>();
        judgeConfigMap.put("timeLimit", problemAddRequest.getTimeLimit());
        judgeConfigMap.put("memoryLimit", problemAddRequest.getMemoryLimit());
        try {
            problem.setJudgeConfig(objectMapper.writeValueAsString(judgeConfigMap));
        } catch (JsonProcessingException e) {
            log.error("判题配置 JSON 序列化失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题配置序列化失败：" + e.getMessage());
        }
        
        // 设置默认状态为公开
        problem.setStatus(1);
        
        // 3. 保存到数据库
        boolean result = this.save(problem);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "添加题目失败");
        
        log.info("新增题目成功，题目 ID: {}", problem.getId());

        // 4. 保存样例输入输出文件
        problemFileService.saveSampleFiles(
                problem.getId(),
                problemAddRequest.getSampleInput(),
                problemAddRequest.getSampleOutput()
        );

        // 5. 绑定标签
        if (CollectionUtils.isNotEmpty(problemAddRequest.getTagIds())) {
            for (Long tagId : problemAddRequest.getTagIds()) {
                ProblemTagMap problemTagMap = ProblemTagMap.builder()
                        .problemId(problem.getId())
                        .tagId(tagId)
                        .build();
                problemTagMapService.save(problemTagMap);
                log.info("绑定标签成功，题目 ID: {}, 标签 ID: {}", problem.getId(), tagId);
            }
        }
        return problem.getId();
    }

    @Override
    public PageResponse<QueryAllProblemResponse> queryAllProblems(PageRequest pageRequest) {
        log.info("查询所有题目，pageNum: {}, pageSize: {}", pageRequest.getPageNum(), pageRequest.getPageSize());
        
        // 构建查询条件（可以添加更多过滤条件）
        LambdaQueryWrapper<Problem> queryWrapper = new LambdaQueryWrapper<>();
        
        // 排序处理
        if (pageRequest.getSortField() != null && !pageRequest.getSortField().isEmpty()) {
            if ("asc".equalsIgnoreCase(pageRequest.getSortOrder())) {
                queryWrapper.orderByAsc(Problem::getGmtCreate);
            } else {
                queryWrapper.orderByDesc(Problem::getGmtCreate);
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(Problem::getGmtCreate);
        }
        
        // 分页查询
        Page<Problem> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Problem> resultPage = this.page(page, queryWrapper);
        
        // 转换为响应对象
        List<QueryAllProblemResponse> responseList = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .toList();
        
        // 构建分页响应
        PageResponse<QueryAllProblemResponse> response = new PageResponse<>();
        response.setPageNum(Math.toIntExact(resultPage.getCurrent()));
        response.setPageSize((int) resultPage.getSize());
        response.setTotal(resultPage.getTotal());
        response.setList(responseList);
        response.setHasPrevious(resultPage.getCurrent() > 1);
        response.setHasNext(resultPage.getCurrent() < resultPage.getPages());
        
        log.info("查询所有题目成功，总数：{}, 当前页：{}", resultPage.getTotal(), resultPage.getCurrent());
        return response;
    }

    @Override
    public PageResponse<QueryAllProblemResponse> searchProblems(Integer pageNum, Integer pageSize,
                                                                SearchProblemsRequest searchProblemsRequest) {
        log.info("搜索题目，pageNum: {}, pageSize: {}, keyword: {}", pageNum, pageSize, searchProblemsRequest);
        
        // 构建查询条件
        LambdaQueryWrapper<Problem> queryWrapper = new LambdaQueryWrapper<>();

        // 按标题搜索
        if (StringUtils.isNotBlank(searchProblemsRequest.getTitle())) {
            queryWrapper.like(Problem::getTitle, searchProblemsRequest.getTitle());
        }

        // 按难度搜索
        if (StringUtils.isNotBlank(searchProblemsRequest.getDifficulty())) {
            queryWrapper.eq(Problem::getDifficulty, searchProblemsRequest.getDifficulty());
        }

        // 按照编号升序排列
        queryWrapper.orderByAsc(Problem::getId);
        
        // 分页查询
        Page<Problem> page = new Page<>(pageNum, pageSize);
        Page<Problem> resultPage = this.page(page, queryWrapper);
        
        // 转换为响应对象
        List<QueryAllProblemResponse> responseList = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .toList();
        
        // 构建分页响应
        PageResponse<QueryAllProblemResponse> response = new PageResponse<>();
        response.setPageNum(Math.toIntExact(resultPage.getCurrent()));
        response.setPageSize((int) resultPage.getSize());
        response.setTotal(resultPage.getTotal());
        response.setList(responseList);
        response.setHasPrevious(resultPage.getCurrent() > 1);
        response.setHasNext(resultPage.getCurrent() < resultPage.getPages());
        
        log.info("搜索题目成功，总数：{}, 当前页：{}", resultPage.getTotal(), resultPage.getCurrent());
        return response;
    }

    @Override
    public BaseProblemInfo getProblemById(Long problemId) {
        log.info("查询题目详情，problemId: {}", problemId);
        
        // 1. 根据 ID 查询题目
        Problem problem = this.getById(problemId);
        ThrowUtils.throwIf(problem == null, ErrorCode.RESOURCE_NO_PROBLEM, "题目不存在");
        
        // 2. 转换为 BaseProblemInfo
        BaseProblemInfo baseProblemInfo = new BaseProblemInfo();
        BeanUtils.copyProperties(problem, baseProblemInfo);
        
        // 3. 查询关联标签信息
        List<TagInfo> tagList = getTagListByProblemId(problemId);
        baseProblemInfo.setTags(tagList);
        
        log.info("查询题目详情成功，题目 ID: {}", problemId);
        return baseProblemInfo;
    }

    /**
     * 将 Problem 实体转换为 QueryAllProblemResponse 对象
     */
    private QueryAllProblemResponse convertToResponse(Problem problem) {
        QueryAllProblemResponse response = new QueryAllProblemResponse();
        BeanUtils.copyProperties(problem, response);
            
        // 查询关联标签信息
        List<TagInfo> tagList = getTagListByProblemId(problem.getId());
        response.setTags(tagList);

        return response;
    }
    
    /**
     * 根据题目 ID 查询关联的标签列表
     * @param problemId 题目 ID
     * @return 标签列表
     */
    private List<TagInfo> getTagListByProblemId(Long problemId) {
        // 1. 通过 ProblemTagMap 查询所有关联的 tagId
        LambdaQueryWrapper<ProblemTagMap> mapQueryWrapper = new LambdaQueryWrapper<>();
        mapQueryWrapper.eq(ProblemTagMap::getProblemId, problemId);
        List<ProblemTagMap> problemTagMaps = problemTagMapService.list(mapQueryWrapper);
            
        if (CollectionUtils.isEmpty(problemTagMaps)) {
            return Collections.emptyList();
        }
            
        // 2. 提取所有的 tagId
        List<Long> tagIds = problemTagMaps.stream()
                .map(ProblemTagMap::getTagId)
                .toList();
            
        // 3. 根据 tagId 查询标签信息
        LambdaQueryWrapper<TagInfo> tagQueryWrapper = new LambdaQueryWrapper<>();
        tagQueryWrapper.in(TagInfo::getId, tagIds);
        return tagService.list(tagQueryWrapper);
    }
}
