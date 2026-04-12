package com.zhy.auraojbackend.judge;

import cn.hutool.core.lang.UUID;
import com.zhy.auraojbackend.common.Constants;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.model.dto.submission.TestCaseJudgeDTO;
import com.zhy.auraojbackend.model.dto.submission.ToJudgeDTO;
import com.zhy.auraojbackend.model.dto.submission.request.TestCaseDebugRequest;
import com.zhy.auraojbackend.model.dto.submission.response.TestCaseDebugResponse;
import com.zhy.auraojbackend.model.entity.Submission;
import com.zhy.auraojbackend.model.enums.SubmissionStatusEnum;
import com.zhy.auraojbackend.service.SubmissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
@Component
@Slf4j
public class Dispatcher {

    @Resource
    private SubmissionService submissionService;

    @Resource
    private RestTemplate restTemplate;

    // 每个提交任务尝试300次失败则判为提交失败
    protected static final Integer MAX_TRY_NUM = 300;
    private static final Map<String, Future> FUTURE_TASK_MAP = new ConcurrentHashMap<>(20);
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(20);


    public Dispatcher(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public Result dispatch(Constants.TaskType taskType, Object data) {
        switch (taskType) {
            case JUDGE:
                defaultJudge((ToJudgeDTO) data, taskType.getPath());
                break;
            case TEST_JUDGE:
                // testJudge 已改为同步调用，不再通过此方法
                break;
            default:
                throw new IllegalArgumentException("判题机不支持此调用类型");
        }
        return null;
    }

    /**
     * 普通评测
     * @param data 判题数据
     * @param path 判题接口路径
     */
    public void defaultJudge(ToJudgeDTO data, String path) {
        Long submitId = data.getSubmission().getId();
        AtomicInteger count = new AtomicInteger(0);
        String taskKey = UUID.randomUUID().toString() + submitId;

        Runnable getResultTask = () -> {
            if (count.get() > MAX_TRY_NUM) {
                checkResult(null, submitId);
                releaseTaskThread(taskKey);
                return;
            }
            count.getAndIncrement();
            Result result = null;
            try {
                // 使用连接池优化的 RestTemplate 发送请求
                result = restTemplate.postForObject(Constants.JUDGE_SERVER_URL + path, data, Result.class);
                log.debug("判题请求成功：submitId={}, taskKey={}", submitId, taskKey);
            } catch (Exception e) {
                log.error("判题服务连接失败，请重新提交或联系管理员！submitId={}, taskKey={}", submitId, taskKey, e);
            } finally {
                checkResult(result, submitId);
                releaseTaskThread(taskKey);
            }
        };
        // 每 2 秒轮询一次判题机
        ScheduledFuture<?> scheduledFuture = SCHEDULER.scheduleWithFixedDelay(getResultTask, 0, 2, TimeUnit.SECONDS);
        FUTURE_TASK_MAP.put(taskKey, scheduledFuture);
    }

    private void checkResult(Result<Void> result, Long submitId) {
        Submission submission = new Submission();
        if (result == null) {
            // 调用失败
            submission.setId(submitId);
            submission.setStatus(SubmissionStatusEnum.SUBMITTED_FAILED);
            submission.setErrorMessage("判题服务连接失败，请重新提交或联系管理员！");
            submissionService.updateById(submission);
        } else {
            if (result.getCode() != ErrorCode.SUCCESS.getCode()) {
                submission.setId(submitId);
                submission.setStatus(SubmissionStatusEnum.SYSTEM_ERROR);
                submission.setErrorMessage(result.getMessage());
                submissionService.updateById(submission);
            }
        }
    }

    private void releaseTaskThread(String taskKey) {
        Future future = FUTURE_TASK_MAP.get(taskKey);
        if (future != null) {
            boolean cancel = future.cancel(true);
            if (cancel) {
                FUTURE_TASK_MAP.remove(taskKey);
            }
        }
    }

    /**
     * 在线调试：同步调用判题服务并返回结果
     *
     * @param debugRequest 调试请求
     * @return 调试结果
     */
    public TestCaseDebugResponse testJudge(TestCaseDebugRequest debugRequest) {
        try {
            log.info("调用判题服务进行在线调试");

            // 构建判题服务请求 DTO
            TestCaseJudgeDTO judgeDTO = new TestCaseJudgeDTO()
                    .setCode(debugRequest.getCode())
                    .setLanguage(debugRequest.getLanguage())
                    .setInput(debugRequest.getTestCaseInput())
                    .setOutput(debugRequest.getExpectedOutput())
                    .setMaxCpuTime(debugRequest.getMaxCpuTime())
                    .setMaxMemory(debugRequest.getMaxMemory());

            // 同步调用判题服务
            String url = Constants.JUDGE_SERVER_URL + Constants.TaskType.TEST_JUDGE.getPath();
            Result result = restTemplate.postForObject(url, judgeDTO, Result.class);

            if (result == null) {
                log.error("判题服务返回为空");
                return TestCaseDebugResponse.builder()
                        .status(SubmissionStatusEnum.SYSTEM_ERROR.getStatus())
                        .errorMessage("判题服务无响应")
                        .build();
            }

            if (result.getCode() != ErrorCode.SUCCESS.getCode()) {
                log.error("判题服务返回错误：{}", result.getMessage());
                return TestCaseDebugResponse.builder()
                        .status(SubmissionStatusEnum.SYSTEM_ERROR.getStatus())
                        .errorMessage(result.getMessage())
                        .build();
            }

            // 将判题服务返回的结果转换为响应 DTO
            // 假设判题服务返回的数据结构与 TestCaseDebugResult 一致
            Object data = result.getData();
            if (data == null) {
                return TestCaseDebugResponse.builder()
                        .status(SubmissionStatusEnum.SYSTEM_ERROR.getStatus())
                        .errorMessage("判题服务返回数据为空")
                        .build();
            }

            // 使用 Hutool 进行对象转换
            cn.hutool.json.JSONObject jsonResult = cn.hutool.json.JSONUtil.parseObj(data);
            
            TestCaseDebugResponse response = TestCaseDebugResponse.builder()
                    .status(jsonResult.getStr("status"))
                    .output(jsonResult.getStr("output"))
                    .stderr(jsonResult.getStr("stderr"))
                    .compileError(jsonResult.getStr("compileError"))
                    .timeUsed(jsonResult.getLong("timeUsed"))
                    .memoryUsed(jsonResult.getLong("memoryUsed"))
                    .isCorrect(jsonResult.getBool("isCorrect"))
                    .errorMessage(jsonResult.getStr("errorMessage"))
                    .build();

            log.info("在线调试完成：status={}", response.getStatus());
            return response;

        } catch (Exception e) {
            log.error("在线调试异常：{}", e.getMessage(), e);
            return TestCaseDebugResponse.builder()
                    .status(SubmissionStatusEnum.SYSTEM_ERROR.getStatus())
                    .errorMessage("在线调试异常：" + e.getMessage())
                    .build();
        }
    }
}
