package com.zhy.auraojbackend.model.dto.user.response;

import com.zhy.auraojbackend.model.dto.user.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录响应体
 *
 * @author zhy
 * @date 2026/3/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginResponse extends BaseUserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * Sa-Token令牌
     */
    private String token;
}