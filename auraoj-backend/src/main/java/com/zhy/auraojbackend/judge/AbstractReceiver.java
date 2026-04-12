package com.zhy.auraojbackend.judge;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/29
 */
public abstract class AbstractReceiver {
    public void handleWaitingTask(String... queues) {
        // 公平轮询策略：每轮每个队列处理一个任务，保证各类型任务均衡处理
        boolean hasTask = true;
        while (hasTask) {
            hasTask = false;
            for (String queue : queues) {
                String task = getTaskByRedis(queue);
                if (task != null) {
                    hasTask = true;
                    handleJudgeTask(task, queue);
                    // 处理完一个任务后，继续检查下一个队列（公平轮询）
                }
            }
        }
    }

    public abstract String getTaskByRedis(String queue);
    public abstract void handleJudgeTask(String task, String queue);
}
