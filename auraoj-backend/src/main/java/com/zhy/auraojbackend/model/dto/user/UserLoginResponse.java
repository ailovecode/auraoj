package com.zhy.auraojbackend.model.dto.user;

import com.zhy.auraojbackend.model.enums.UserRoleEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录响应体
 *
 * @author zhy
 * @date 2026/3/1
 */
@Data
public class UserLoginResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 学校
     */
    private String school;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 角色权限
     */
    private UserRoleEnum role;

    /**
     * 用户等级
     */
    private Integer level;

    /**
     * 积分
     */
    private Integer score;

    /**
     * 账号状态
     */
    private Integer status;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * Sa-Token令牌
     */
    private String token;
}