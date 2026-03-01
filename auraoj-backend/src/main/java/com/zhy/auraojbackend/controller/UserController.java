package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.zhy.auraojbackend.common.BaseResponse;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.ResultUtils;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.user.UserLoginRequest;
import com.zhy.auraojbackend.model.dto.user.UserLoginResponse;
import com.zhy.auraojbackend.model.dto.user.UserRegisterRequest;
import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/2/13 21:58
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户管理接口")
@Slf4j
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    @SaIgnore
    @ApiResponse(responseCode = "200", description = "注册成功",
            content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<Long> userRegister(
            @Parameter(description = "注册请求参数") @RequestBody UserRegisterRequest userRegisterRequest,
            HttpServletRequest request) {
        try {
            Long result = userInfoService.userRegister(userRegisterRequest);
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    @SaIgnore
    @ApiResponse(responseCode = "200", description = "登录成功",
            content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<UserLoginResponse> userLogin(
            @Parameter(description = "登录请求参数") @RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request) {
        try {
            UserLoginResponse result = userInfoService.userLogin(userLoginRequest);
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    @SaIgnore
    @ApiResponse(responseCode = "200", description = "登出成功",
            content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        try {
            boolean result = userInfoService.userLogout();
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登出异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    @SaCheckLogin
    public BaseResponse<UserInfo> getCurrentUser(HttpServletRequest request) {
        try {
            UserInfo currentUser = userInfoService.getCurrentUser();
            return ResultUtils.success(currentUser);
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取当前用户异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}