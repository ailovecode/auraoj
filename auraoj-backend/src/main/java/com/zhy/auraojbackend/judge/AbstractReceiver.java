package com.zhy.auraojbackend.judge;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
public abstract class AbstractReceiver {
    public void handleWaitingTask(String... queues) {
        for (String queue : queues) {
            String task = getTaskByRedis(queue);
            if (task != null) {
                handleJudgeTask(task,  queue);
            }
        }
    }

    public abstract String getTaskByRedis(String queue);
    public abstract void handleJudgeTask(String task, String queue);
}
