package com.zhy.auraojbackend.model.dto.user.request;

import com.zhy.auraojbackend.model.dto.AbstractCheckRequest;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户信息更新请求参数
 * @author zhy
 */
@Data
@Schema(description = "用户信息更新请求参数")
public class UserUpdateRequest implements Serializable, AbstractCheckRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 登录用户名，唯一
     */
    @Size(min = 4, max = 50, message = "用户名长度应为 4-50 位")
    @Schema(description = "登录用户名", example = "zhangsan")
    private String username;

    /**
     * 中国大陆手机号，唯一
     */
    @Size(max = 11, message = "手机号长度不能超过 11 位")
    @Schema(description = "中国大陆手机号", example = "13800138000")
    private String phone;

    /**
     * 邮箱地址，唯一
     */
    @Size(max = 100, message = "邮箱地址长度不能超过 100 个字符")
    @Schema(description = "邮箱地址", example = "example@email.com")
    private String email;

    /**
     * 密码
     */
    @Size(min = 6, max = 20, message = "密码长度应为 6-20 位")
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 确认密码
     */
    @Size(min = 6, max = 20, message = "密码长度应为 6-20 位")
    @Schema(description = "确认密码", example = "123456")
    private String confirmPassword;
    
    /**
     * 头像URL 地址
     */
    @Size(max = 500, message = "头像URL 长度不能超过 500 个字符")
    @Schema(description = "头像URL 地址", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 性别：-1未知, 0女, 1男
     */
    @Min(value = -1, message = "性别值必须为-1、0或1")
    @Max(value = 1, message = "性别值必须为-1、0或1")
    @Schema(description = "性别：-1未知, 0女, 1男", example = "1")
    private Integer gender;

    /**
     * 所属学校/机构
     */
    @Size(max = 100, message = "学校名称长度不能超过100个字符")
    @Schema(description = "所属学校/机构", example = "清华大学")
    private String school;

    /**
     * 个性签名
     */
    @Size(max = 200, message = "个性签名长度不能超过200个字符")
    @Schema(description = "个性签名", example = "热爱编程，追求卓越")
    private String signature;

    @Override
    public void check() {
        // 校验用户名长度（如果提供了用户名）
        if (StringUtils.isNotBlank(username) && (username.length() < 4 || username.length() > 50)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "用户名长度应为 4-50 位");
        }
    
        // 校验手机号格式（如果提供了手机号）
        if (StringUtils.isNotBlank(phone) && !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "手机号格式不正确");
        }
    
        // 校验邮箱格式（如果提供了邮箱）
        if (StringUtils.isNotBlank(email) && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "邮箱格式不正确");
        }

        // 校验两次输入的密码是否一致
        if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(confirmPassword)
                && !password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "两次输入的密码不一致");
        }

        // 校验性别值范围
        if (gender != null && gender != -1 && gender != 0 && gender != 1) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "性别值只能为 -1、0 或 1");
        }
    }
}