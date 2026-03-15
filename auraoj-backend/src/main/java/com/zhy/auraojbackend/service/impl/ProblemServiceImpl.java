package com.zhy.auraojbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.mapper.ProblemMapper;
import com.zhy.auraojbackend.model.dto.problem.ProblemAddRequest;
import com.zhy.auraojbackend.model.entity.Problem;
import com.zhy.auraojbackend.model.entity.ProblemTagMap;
import com.zhy.auraojbackend.service.ProblemFileService;
import com.zhy.auraojbackend.service.ProblemService;
import com.zhy.auraojbackend.service.ProblemTagMapService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
}
