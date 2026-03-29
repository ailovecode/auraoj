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

    private final ExecutorService virtualExecutor =
            Executors.newVirtualThreadPerTaskExecutor();

    public void processWaitingTask() {
        virtualExecutor.submit(() -> {
            // 优先处理比赛的提交任务
            // 其次处理普通提交的提交任务
            // 最后处理在线调试的任务
            handleWaitingTask(Constants.Queue.CONTEST_JUDGE_WAITING.getName(),
                    Constants.Queue.GENERAL_JUDGE_WAITING.getName(),
                    Constants.Queue.TEST_JUDGE_WAITING.getName());
        });
    }

    @Override
    public String getTaskByRedis(String queue) {
        long size = redisUtils.lGetListSize(queue);
        if (size > 0) {
            return (String) redisUtils.lrPop(queue);
        }
        return null;
    }

    @Override
    public void handleJudgeTask(String task, String queue) {
        try {
            if (Constants.Queue.TEST_JUDGE_WAITING.getName().equals(queue)) {
                // TODO: 处理在线调试任务
                log.info("处理在线调试任务：queue={}", queue);
            } else {
                JSONObject taskJson = JSONUtil.parseObj(task);
                Long judgeId = taskJson.getLong("judgeId", 0L);
                
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
                        .setSubmission(submission)
                        .setIsContest(submission.getContestId() != null);
                
                dispatcher.dispatch(Constants.TaskType.JUDGE, toJudgeDTO);
            }
        } catch (Exception e) {
            log.error("处理判题任务异常：task={}, error={}", task, e.getMessage(), e);
        }

        // 处理下一个任务
        processWaitingTask();
    }
}
