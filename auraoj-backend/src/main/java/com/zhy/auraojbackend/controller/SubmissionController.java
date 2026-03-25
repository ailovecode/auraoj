package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.submission.request.SubmitRequest;
import com.zhy.auraojbackend.model.dto.submission.response.SubmitResponse;
import com.zhy.auraojbackend.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/25
 */
@RestController
@RequestMapping("/api/submission")
@Tag(name = "提交管理", description = "提交代码相关接口")
@Slf4j
public class SubmissionController {

    @Resource
    private SubmissionService submissionService;

    @PostMapping("/submit")
    @Operation(summary = "提交代码", description = "创建一条提交记录并返回简要信息")
    @SaCheckLogin
    public Result<SubmitResponse> submit(
            @Parameter(description = "提交请求") @RequestBody SubmitRequest submitRequest,
            HttpServletRequest request) {
        try {
            SubmitResponse submitResponse = submissionService.submit(submitRequest);
            return Result.success(submitResponse, "ok");
        } catch (BusinessException e) {
            log.error("提交代码业务错误", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("提交代码参数错误", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("提交代码系统错误", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}