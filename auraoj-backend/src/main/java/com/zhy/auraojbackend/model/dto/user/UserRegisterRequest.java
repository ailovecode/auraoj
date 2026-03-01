package com.zhy.auraojbackend.model.dto.user;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.common.ErrorCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author zhy
 * @date 2026/3/1
 */
@Data
public class UserRegisterRequest implements Serializable, AbstractCheckRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 登录用户名，唯一
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;

    /**
     * 中国大陆手机号，唯一
     */
    private String phone;

    /**
     * 邮箱地址，唯一
     */
    private String email;

    /**
     * 性别：-1未知, 0女, 1男
     */
    private Integer gender;

    /**
     * 所属学校/机构
     */
    private String school;

    @Override
    public void check() {
        // 校验参数非空
        if (StringUtils.isAnyBlank(username, password, confirmPassword)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "参数为空");
        }

        // 校验用户名长度
        if (username.length() < 4 || username.length() > 20) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "用户名长度为4-20位");
        }

        // 校验密码长度
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "密码长度为6-20位");
        }

        // 校验两次输入的密码是否一致
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "两次输入的密码不一致");
        }

        // 校验手机号格式
        if (StringUtils.isNotBlank(phone) && !phone.matches("^[1][3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "手机号格式错误");
        }

        // 校验邮箱格式
        if (StringUtils.isNotBlank(email) && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "邮箱格式错误");
        }
    }
}