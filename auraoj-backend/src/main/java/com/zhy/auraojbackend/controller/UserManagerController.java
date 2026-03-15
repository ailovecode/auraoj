package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.PageRequest;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.user.request.UserLoginRequest;
import com.zhy.auraojbackend.model.dto.user.response.UserLoginResponse;
import com.zhy.auraojbackend.model.dto.user.request.UserRegisterRequest;
import com.zhy.auraojbackend.model.dto.user.request.UserUpdateRequest;
import com.zhy.auraojbackend.model.vo.UserInfoVO;
import com.zhy.auraojbackend.service.MinioService;
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
import org.springframework.web.multipart.MultipartFile;


/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/2/13 21:58
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户管理接口")
@Slf4j
public class UserManagerController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private MinioService minioService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    @SaIgnore
    @ApiResponse(responseCode = "200", description = "注册成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    public Result<Long> userRegister(
            @Parameter(description = "注册请求参数") @RequestBody UserRegisterRequest userRegisterRequest,
            HttpServletRequest request) {
        try {
            Long result = userInfoService.userRegister(userRegisterRequest);
            return Result.success(result, "注册成功!");
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    @SaIgnore
    @ApiResponse(responseCode = "200", description = "登录成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    public Result<UserLoginResponse> userLogin(
            @Parameter(description = "登录请求参数") @RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request) {
        try {
            UserLoginResponse result = userInfoService.userLogin(userLoginRequest);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    @SaIgnore
    @ApiResponse(responseCode = "200", description = "登出成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    public Result<Boolean> userLogout(HttpServletRequest request) {
        try {
            boolean result = userInfoService.userLogout();
            return Result.success(result, "登出成功");
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登出异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @SaCheckLogin
    public Result<UserInfoVO> getCurrentUser(HttpServletRequest request) {
        try {
            UserInfoVO currentUser = userInfoService.getCurrentUser();
            return Result.success(currentUser);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取当前用户异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的基本信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @SaCheckLogin
    public Result<UserInfoVO> updateCurrentUser(
            @Parameter(description = "用户信息更新参数") @RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        try {
            UserInfoVO result = userInfoService.updateCurrentUser(userUpdateRequest);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/admin/{userId}/update")
    @Operation(summary = "管理员更新指定用户信息", description = "更新指定用户的基本信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<UserInfoVO> updateCurrentUser(
            @Parameter(description = "目标用户 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "用户信息更新参数") @RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        try {
            UserInfoVO result = userInfoService.adminUpdateUser(userId, userUpdateRequest);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/admin/list")
    @Operation(summary = "获取所有用户信息（分页）", description = "管理员和教师分页获取系统中所有用户的信息列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<PageResponse<UserInfoVO>> getAllUsers(
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPageNum(pageNum);
            pageRequest.setPageSize(pageSize);
            
            PageResponse<UserInfoVO> result = userInfoService.getAllUsers(pageRequest);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取所有用户异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @DeleteMapping("/admin/{userId}/deleteUser")
    @Operation(summary = "删除用户", description = "管理员删除指定用户（软删除，将状态设置为封禁）")
    @ApiResponse(responseCode = "200", description = "删除成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Boolean> deleteUser(
            @Parameter(description = "目标用户 ID", required = true) @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            boolean result = userInfoService.deleteUser(userId);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除用户异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/updateUserAvatar")
    @Operation(summary = "上传用户头像", description = "上传当前登录用户的头像图片")
    @ApiResponse(responseCode = "200", description = "上传成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @SaCheckLogin
    public Result<UserInfoVO> updateUserAvatar(
            @Parameter(description = "目标用户 ID", required = true) @RequestParam("userId") Long userId,
            @Parameter(description = "头像文件", required = true) @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            UserInfoVO result = userInfoService.updateUserAvatar(userId, file);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("上传头像异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }


}