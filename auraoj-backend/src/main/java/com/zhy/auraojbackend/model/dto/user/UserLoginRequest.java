package com.zhy.auraojbackend.model.dto.user;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.common.ErrorCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author zhy
 * @date 2026/3/1
 */
@Data
public class UserLoginRequest implements Serializable, AbstractCheckRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 登录用户名或邮箱
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    @Override
    public void check() {
        // 校验参数非空
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "参数为空");
        }

        // 校验用户名长度
        if (username.length() < 4 || username.length() > 50) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "用户名长度为4-50位");
        }

        // 校验密码长度
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "密码长度为6-20位");
        }
    }
}