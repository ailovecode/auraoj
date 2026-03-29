package com.zhy.auraojbackend.judge;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhy.auraojbackend.common.Constants;
import com.zhy.auraojbackend.model.dto.submission.ToJudgeDTO;
import com.zhy.auraojbackend.model.entity.Submission;
import com.zhy.auraojbackend.service.SubmissionService;
import com.zhy.auraojbackend.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
@Component
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
        if (Constants.Queue.TEST_JUDGE_WAITING.getName().equals(queue)) {
            // 单独调用
        } else {
            JSONObject taskJson = JSONUtil.parseObj(task);
            Long judgeId = taskJson.getLong("judgeId");
            Submission submission = submissionService.getById(judgeId);
            /*if (submission != null) {
                if(SubmissionStatusEnum.CANCELLED.equals(submission.getStatus())) {
                    if(submission.getContestId() != null) {
                        LambdaUpdateWrapper<Submission> wrapper = new LambdaUpdateWrapper<>();

                    }
                }
            }*/
            // 调用评测服务
            dispatcher.dispatch(Constants.TaskType.JUDGE, new ToJudgeDTO().setSubmission(submission));
        }

        // 处理下一个任务
        processWaitingTask();
    }
}
