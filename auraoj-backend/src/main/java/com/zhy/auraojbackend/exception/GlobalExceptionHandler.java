package com.zhy.auraojbackend.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import com.zhy.auraojbackend.common.BaseResponse;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/1
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, ErrorCode.SYSTEM_ERROR.getMessage());
    }

    // 拦截：未登录异常
    @ExceptionHandler(NotLoginException.class)
    public BaseResponse<?> handlerException(NotLoginException e) {
        log.error("NotLoginException: ", e);
        return ResultUtils.error(ErrorCode.NO_LOGIN, ErrorCode.NO_LOGIN.getMessage());
    }

    // 拦截：缺少角色异常
    @ExceptionHandler(NotRoleException.class)
    public BaseResponse<?> handlerException(NotRoleException e) {
        log.error("NotRoleException: ", e);
        return ResultUtils.error(ErrorCode.ACCESS_DENIED,"您没有" + e.getRole() + "权限哦！请联系管理员！");
    }
}