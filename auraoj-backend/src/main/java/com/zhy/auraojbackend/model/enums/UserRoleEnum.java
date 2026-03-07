package com.zhy.auraojbackend.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/1
 */
public enum UserRoleEnum {

    STUDENT("student", "学生", 1),
    TEACHER("teacher", "教师", 2),
    ADMIN("admin", "管理员", 3);

    @EnumValue
    @JsonValue
    private final String value;
    private final String description;
    private final Integer label;

    UserRoleEnum(String value, String description, Integer label) {
        this.value = value;
        this.description = description;
        this.label = label;
    }

    /**
     * 获取枚举值列表
     */
    private static final List<String> VALUES = Arrays.stream(values())
            .map(item -> item.value)
            .toList();
    public static List<String> getValues() {
        return VALUES;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 枚举值
     * @return 枚举
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        return null;
    }


    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLabel() {
        return label;
    }
}
