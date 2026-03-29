package com.zhy.auraojbackend.judge;

import cn.hutool.json.JSONObject;
import com.zhy.auraojbackend.common.Constants;
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

    public void sendTask(Long submissionId, Long problemId, Boolean isContest) {
        JSONObject task = new JSONObject();
        task.put("submissionId", submissionId);
        task.put("isContest", isContest);
        try {
            boolean isOk;
            if (Boolean.TRUE.equals(isContest)) {
                isOk = redisUtils.llPush(Constants.Queue.CONTEST_JUDGE_WAITING.getName(), task);
            } else {
                isOk = redisUtils.llPush(Constants.Queue.GENERAL_JUDGE_WAITING.getName(), task);
            }
            if (!isOk) {
                submissionService.updateById(new Submission()
                        .setId(submissionId)
                        .setStatus(SubmissionStatusEnum.SUBMITTED_FAILED)
                        .setErrorMessage("注入缓存队列失败，请重新提交！"));
            }
            // 处理缓存队列
            judgeReceiver.processWaitingTask();
        } catch (Exception e) {
            log.error("判题服务异常：{}", e.getMessage());
            submissionService.updateById(new Submission()
                    .setId(submissionId)
                    .setStatus(SubmissionStatusEnum.SUBMITTED_FAILED)
                    .setErrorMessage("判题服务异常，请重新提交！"));
            // todo: 同步更新比赛提交记录

        }
    }
}
