package com.zhy.auraojbackend.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/15
 */
@Getter
public enum DifficultyLevelEnum {
    EASY("easy", "简单"),
    MEDIUM("medium", "中等"),
    HARD("hard", "困难");

    @EnumValue
    @JsonValue
    private final String level;
    private final String description;

    DifficultyLevelEnum(String level, String description) {
        this.level = level;
        this.description = description;
    }
}
