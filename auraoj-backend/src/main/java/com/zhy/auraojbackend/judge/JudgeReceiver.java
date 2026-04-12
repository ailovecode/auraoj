package com.zhy.auraojbackend.judge;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhy.auraojbackend.common.Constants;
import com.zhy.auraojbackend.model.dto.submission.ToJudgeDTO;
import com.zhy.auraojbackend.model.entity.Submission;
import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import com.zhy.auraojbackend.service.SubmissionService;
import com.zhy.auraojbackend.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
@Component
@Slf4j
public class JudgeReceiver extends AbstractReceiver {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private SubmissionService submissionService;
    @Resource
    private Dispatcher dispatcher;

    private final AtomicInteger activeThreads = new AtomicInteger(0);

    private final ExecutorService virtualExecutor =
            Executors.newVirtualThreadPerTaskExecutor();

    public void processWaitingTask() {
        virtualExecutor.submit(() -> {
            int count = activeThreads.incrementAndGet();
            log.debug("当前活跃判题线程数: {}", count);
            try {
                handleWaitingTask(Constants.Queue.CONTEST_JUDGE_WAITING.getName(),
                        Constants.Queue.GENERAL_JUDGE_WAITING.getName(),
                        Constants.Queue.TEST_JUDGE_WAITING.getName());
            } finally {
                activeThreads.decrementAndGet();
            }
        });
    }

    @Override
    public String getTaskByRedis(String queue) {
        long size = redisUtils.lGetListSize(queue);
        if (size > 0) {
            Object o = redisUtils.lrPop(queue);
            return JSONUtil.toJsonStr(o);
        }
        return null;
    }

    @Override
    public void handleJudgeTask(String task, String queue) {
        try {
            if (Constants.Queue.TEST_JUDGE_WAITING.getName().equals(queue)) {
                // 处理在线调试任务
                handleTestJudgeTask(task);
                return;
            }
            
            JSONObject taskJson = JSONUtil.parseObj(task);
            Long judgeId = taskJson.getLong("submissionId", 0L);
            
            if (judgeId == null || judgeId <= 0) {
                log.error("无效的判题 ID：task={}", task);
                return;
            }
            
            Submission submission = submissionService.getById(judgeId);
            if (submission == null) {
                log.error("提交记录不存在：judgeId={}", judgeId);
                return;
            }
            
            // 检查提交是否已被取消
            if (SubmissionStatusEnum.CANCELLED.equals(submission.getStatus())) {
                log.info("提交已被取消，跳过判题：judgeId={}", judgeId);
                return;
            }
            
            log.info("开始处理判题任务：judgeId={}, problemId={}", 
                    judgeId, submission.getProblemId());
            
            // 调用评测服务分发任务
            ToJudgeDTO toJudgeDTO = new ToJudgeDTO()
                    .setSubmission(submission);
            dispatcher.dispatch(Constants.TaskType.JUDGE, toJudgeDTO);
        } catch (Exception e) {
            log.error("处理判题任务异常：task={}, error={}", task, e.getMessage(), e);
        }
    }
    
    /**
     * 处理在线调试任务
     *
     * @param task 调试任务 JSON
     */
    private void handleTestJudgeTask(String task) {
        JSONObject taskJson = JSONUtil.parseObj(task);
        String requestId = taskJson.getStr("requestId");
        String resultKey = "test_judge_result:" + requestId;
        
        try {
            log.info("开始处理在线调试任务：requestId={}", requestId);
            
            // 构建调试请求
            com.zhy.auraojbackend.model.dto.submission.request.TestCaseDebugRequest debugRequest = 
                new com.zhy.auraojbackend.model.dto.submission.request.TestCaseDebugRequest();
            debugRequest.setCode(taskJson.getStr("code"));
            debugRequest.setLanguage(taskJson.getStr("language"));
            debugRequest.setTestCaseInput(taskJson.getStr("testCaseInput"));
            debugRequest.setExpectedOutput(taskJson.getStr("expectedOutput"));
            debugRequest.setMaxCpuTime(taskJson.getLong("maxCpuTime"));
            debugRequest.setMaxMemory(taskJson.getLong("maxMemory"));
            
            // 调用 Dispatcher 进行判题
            com.zhy.auraojbackend.model.dto.submission.response.TestCaseDebugResponse response = 
                dispatcher.testJudge(debugRequest);
            
            // 将结果写入 Redis，设置过期时间为 60 秒
            cn.hutool.json.JSONObject resultJson = cn.hutool.json.JSONUtil.parseObj(response);
            redisUtils.set(resultKey, resultJson, 60, java.util.concurrent.TimeUnit.SECONDS);
            
            log.info("在线调试任务完成：requestId={}, status={}", requestId, response.getStatus());
            
        } catch (Exception e) {
            log.error("处理在线调试任务异常：requestId={}, error={}", requestId, e.getMessage(), e);
            
            // 写入错误结果
            com.zhy.auraojbackend.model.dto.submission.response.TestCaseDebugResponse errorResponse = 
                com.zhy.auraojbackend.model.dto.submission.response.TestCaseDebugResponse.builder()
                    .status("System Error")
                    .errorMessage("处理调试任务异常：" + e.getMessage())
                    .build();
            cn.hutool.json.JSONObject errorJson = cn.hutool.json.JSONUtil.parseObj(errorResponse);
            redisUtils.set(resultKey, errorJson, 60, java.util.concurrent.TimeUnit.SECONDS);
        }
    }
}
