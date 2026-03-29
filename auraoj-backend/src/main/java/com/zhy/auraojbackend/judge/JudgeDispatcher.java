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
        task.put("problemId", problemId);
        task.put("isContest", isContest);
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
}
