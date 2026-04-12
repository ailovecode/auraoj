package com.zhy.auraojbackend.judge;

import cn.hutool.json.JSONObject;
import com.zhy.auraojbackend.common.Constants;
import com.zhy.auraojbackend.model.dto.submission.request.TestCaseDebugRequest;
import com.zhy.auraojbackend.model.dto.submission.response.TestCaseDebugResponse;
import com.zhy.auraojbackend.model.entity.Submission;
import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import com.zhy.auraojbackend.service.SubmissionService;
import com.zhy.auraojbackend.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
@Component
@Slf4j
public class JudgeDispatcher {

    @Resource
    private SubmissionService submissionService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private JudgeReceiver judgeReceiver;
    @Resource
    private Dispatcher dispatcher;

    public void sendTask(Long submissionId, Long problemId, Boolean isContest) {
        JSONObject task = new JSONObject();
        task.set("submissionId", submissionId);
        task.set("problemId", problemId);
        task.set("isContest", isContest);
        try {
            long queueSize;
            String queueName;
            if (Boolean.TRUE.equals(isContest)) {
                queueName = Constants.Queue.CONTEST_JUDGE_WAITING.getName();
                queueSize = redisUtils.llPushWithResult(queueName, task);
            } else {
                queueName = Constants.Queue.GENERAL_JUDGE_WAITING.getName();
                queueSize = redisUtils.llPushWithResult(queueName, task);
            }

            if (queueSize < 0) {
                log.error("注入判题队列失败：submissionId={}", submissionId);
                submissionService.updateById(new Submission()
                        .setId(submissionId)
                        .setStatus(SubmissionStatusEnum.SUBMITTED_FAILED)
                        .setErrorMessage("注入缓存队列失败，请重新提交！"));
                return;
            }

            log.info("成功注入判题队列：submissionId={}, queue={}, size={}",
                    submissionId, queueName, queueSize);

            // 触发处理缓存队列
            judgeReceiver.processWaitingTask();
        } catch (Exception e) {
            log.error("判题服务异常：submissionId={}, error={}", submissionId, e.getMessage(), e);
            submissionService.updateById(new Submission()
                    .setId(submissionId)
                    .setStatus(SubmissionStatusEnum.SUBMITTED_FAILED)
                    .setErrorMessage("判题服务异常，请重新提交！"));
        }
    }

    /**
     * 发送在线调试任务（通过 Redis 队列异步处理）
     *
     * @param debugRequest 调试请求
     * @return 调试结果
     */
    public TestCaseDebugResponse sendTestCaseDebugTask(TestCaseDebugRequest debugRequest) {
        String requestId = java.util.UUID.randomUUID().toString();
        String resultKey = "test_judge_result:" + requestId;
        
        try {
            log.info("开始处理在线调试请求，requestId={}", requestId);
            
            // 构建调试任务
            JSONObject task = new JSONObject();
            task.set("requestId", requestId);
            task.set("code", debugRequest.getCode());
            task.set("language", debugRequest.getLanguage());
            task.set("testCaseInput", debugRequest.getTestCaseInput());
            task.set("expectedOutput", debugRequest.getExpectedOutput());
            task.set("maxCpuTime", debugRequest.getMaxCpuTime());
            task.set("maxMemory", debugRequest.getMaxMemory());
            
            // 推送到在线调试队列
            long queueSize = redisUtils.llPushWithResult(Constants.Queue.TEST_JUDGE_WAITING.getName(), task);
            if (queueSize < 0) {
                log.error("注入在线调试队列失败：requestId={}", requestId);
                return TestCaseDebugResponse.builder()
                        .status(SubmissionStatusEnum.SYSTEM_ERROR.getStatus())
                        .errorMessage("注入缓存队列失败，请重试！")
                        .build();
            }
            
            log.info("成功注入在线调试队列：requestId={}, queueSize={}", requestId, queueSize);
            
            // 触发处理缓存队列
            judgeReceiver.processWaitingTask();
            
            // 等待结果（最多等待 30 秒）
            return waitForTestResult(resultKey, 30);
            
        } catch (Exception e) {
            log.error("在线调试服务异常：requestId={}, error={}", requestId, e.getMessage(), e);
            return TestCaseDebugResponse.builder()
                    .status(SubmissionStatusEnum.SYSTEM_ERROR.getStatus())
                    .errorMessage("在线调试服务异常：" + e.getMessage())
                    .build();
        } finally {
            // 清理结果缓存
            redisUtils.delete(resultKey);
        }
    }
    
    /**
     * 等待在线调试结果
     *
     * @param resultKey Redis 结果键
     * @param timeoutSeconds 超时时间（秒）
     * @return 调试结果
     */
    private TestCaseDebugResponse waitForTestResult(String resultKey, int timeoutSeconds) {
        for (int i = 0; i < timeoutSeconds * 10; i++) {
            try {
                // 每 100ms 检查一次
                Thread.sleep(100);
                
                Object result = redisUtils.get(resultKey);
                if (result != null) {
                    cn.hutool.json.JSONObject jsonResult = cn.hutool.json.JSONUtil.parseObj(result);
                    return TestCaseDebugResponse.builder()
                            .status(jsonResult.getStr("status"))
                            .output(jsonResult.getStr("output"))
                            .stderr(jsonResult.getStr("stderr"))
                            .compileError(jsonResult.getStr("compileError"))
                            .timeUsed(jsonResult.getLong("timeUsed"))
                            .memoryUsed(jsonResult.getLong("memoryUsed"))
                            .isCorrect(jsonResult.getBool("isCorrect"))
                            .errorMessage(jsonResult.getStr("errorMessage"))
                            .build();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("等待在线调试结果被中断");
                return TestCaseDebugResponse.builder()
                        .status("System Error")
                        .errorMessage("等待结果被中断")
                        .build();
            }
        }
        
        log.error("等待在线调试结果超时");
        return TestCaseDebugResponse.builder()
                .status("System Error")
                .errorMessage("等待结果超时，请稍后重试")
                .build();
    }
}
