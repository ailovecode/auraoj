package com.zhy.auraojbackend.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 提交状态枚举
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/25
 */
@Getter
public enum SubmissionStatusEnum {

    PENDING("Pending", "待评判"),
    ACCEPTED("Accepted", "通过"),
    WRONG_ANSWER("Wrong Answer", "答案错误"),
    COMPILE_ERROR("Compile Error", "编译错误"),
    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceeded", "内存超限"),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded", "超时"),
    PRESENTATION_ERROR("Presentation Error", "格式错误"),
    OUTPUT_LIMIT_EXCEEDED("Output Limit Exceeded", "输出超限"),
    DANGEROUS_OPERATION("Dangerous Operation", "危险操作"),
    RUNTIME_ERROR("Runtime Error", "运行时错误"),
    SYSTEM_ERROR("System Error", "系统错误"),
    CANCELLED("Cancelled", "取消"),
    SUBMITTED_FAILED("Submitted Failed", "提交失败");

    @EnumValue
    @JsonValue
    private final String status;
    private final String description;

    SubmissionStatusEnum(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public static SubmissionStatusEnum getEnumByStatus(String status) {
        for (SubmissionStatusEnum s : values()) {
            if (s.getStatus().equals(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }

    public static SubmissionStatusEnum getEnumByDescription(String description) {
        for (SubmissionStatusEnum status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status description: " + description);
    }
}
