package com.zhy.auraojbackend.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.common.ErrorCode;
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
    public Result<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return Result.error(ErrorCode.SYSTEM_ERROR, ErrorCode.SYSTEM_ERROR.getMessage());
    }

    // 拦截：未登录异常
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handlerException(NotLoginException e) {
        log.error("NotLoginException: ", e);
        return Result.error(ErrorCode.NO_LOGIN, ErrorCode.NO_LOGIN.getMessage());
    }

    // 拦截：缺少角色异常
    @ExceptionHandler(NotRoleException.class)
    public Result<?> handlerException(NotRoleException e) {
        log.error("NotRoleException: ", e);
        return Result.error(ErrorCode.ACCESS_DENIED,"您没有" + e.getRole() + "权限哦！请联系管理员！");
    }
}