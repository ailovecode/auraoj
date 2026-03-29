package com.zhy.auraojbackend.judge;

import cn.hutool.core.lang.UUID;
import com.zhy.auraojbackend.common.Constants;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.model.dto.submission.ToJudgeDTO;
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
                // testJudge((TestJudgeReq) data, taskType.getPath());
                break;
            default:
                throw new IllegalArgumentException("判题机不支持此调用类型");
        }
        return null;
    }

    /**
     * 普通评测
     * @param data
     * @param path
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
                result = restTemplate.postForObject(Constants.JUDGE_SERVER_URL + path, data, Result.class);
            } catch (Exception e) {
                log.error("判题服务连接失败，请重新提交或联系管理员！");
            } finally {
                checkResult(result, submitId);
                releaseTaskThread(taskKey);
            }
        };
        ScheduledFuture<?> scheduledFuture = SCHEDULER.scheduleWithFixedDelay(getResultTask, 0, 2, TimeUnit.SECONDS);
        FUTURE_TASK_MAP.put(taskKey, scheduledFuture);
    }

    private void checkResult(Result<Void> result, Long submitId) {
        Submission submission = new Submission();
        if (result == null) {
            // 调用失败
            submission.setProblemId(submitId);
            submission.setStatus(SubmissionStatusEnum.SUBMITTED_FAILED);
            submission.setErrorMessage("判题服务连接失败，请重新提交或联系管理员！");
            submissionService.updateById(submission);
        } else {
            if (result.getCode() != ErrorCode.SUCCESS.getCode()) {
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
}
