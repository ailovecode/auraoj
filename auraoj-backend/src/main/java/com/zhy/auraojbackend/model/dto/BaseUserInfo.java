package com.zhy.auraojbackend.model.dto;

import com.zhy.auraojbackend.model.enums.UserRoleEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/7
 */
@Data
public class BaseUserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增 ID
     */
    private Long id;

    /**
     * 头像 URL 地址
     */
    private String avatar;

    /**
     * 性别：-1 未知，0 女，1 男
     */
    private Integer gender;

    /**
     * 登录用户名，唯一
     */
    private String username;

    /**
     * 中国大陆手机号
     */
    private String phone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 所属学校/机构
     */
    private String school;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 角色权限：student/teacher/admin
     */
    private UserRoleEnum role;

    /**
     * 用户等级，默认 1
     */
    private Integer level;

    /**
     * 积分/等级分值
     */
    private Integer score;

    /**
     * 账号状态：0 正常，1 封禁/删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 最后修改时间
     */
    private LocalDateTime gmtModified;
}
