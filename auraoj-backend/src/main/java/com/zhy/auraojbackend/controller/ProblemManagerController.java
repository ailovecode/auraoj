package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.problem.ProblemAddRequest;
import com.zhy.auraojbackend.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * @Date 2026/3/15
 */
@RestController
@RequestMapping("/api/problem")
@Tag(name = "题目管理", description = "题目管理接口")
@Slf4j
public class ProblemManagerController {

    @Resource
    private ProblemService problemService;

    @PostMapping("/add")
    @Operation(summary = "新增题目", description = "管理员或教师新增题目")
    @ApiResponse(responseCode = "200", description = "添加成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Long> addProblem(
            @Parameter(description = "新增题目请求参数") 
            @RequestBody ProblemAddRequest problemAddRequest,
            HttpServletRequest request) {
        try {
            Long problemId = problemService.addProblem(problemAddRequest);
            return Result.success(problemId, "题目添加成功!");
        } catch (BusinessException e) {
            log.error("添加题目异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("添加题目异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("添加题目异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}
