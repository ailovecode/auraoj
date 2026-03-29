package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.PageRequest;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.problem.BaseProblemInfo;
import com.zhy.auraojbackend.model.dto.problem.request.ProblemAddRequest;
import com.zhy.auraojbackend.model.dto.problem.request.ProblemUpdateRequest;
import com.zhy.auraojbackend.model.dto.problem.request.SearchProblemsRequest;
import com.zhy.auraojbackend.model.dto.problem.response.QueryAllProblemResponse;
import com.zhy.auraojbackend.model.entity.Problem;
import com.zhy.auraojbackend.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Long> addProblem(
            @Parameter(description = "新增题目请求参数") @RequestBody ProblemAddRequest problemAddRequest,
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

    @PostMapping("/update")
    @Operation(summary = "编辑题目", description = "管理员或教师按传入字段局部更新题目")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Problem> updateProblem(
            @Parameter(description = "编辑题目请求参数") @RequestBody ProblemUpdateRequest problemUpdateRequest,
            HttpServletRequest request) {
        try {
            Problem problem = problemService.updateProblem(problemUpdateRequest);
            return Result.success(problem, "题目编辑成功!");
        } catch (BusinessException e) {
            log.error("编辑题目异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("编辑题目异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("编辑题目异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有题目", description = "管理员或教师查询所有题目列表（分页）")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<PageResponse<QueryAllProblemResponse>> queryAllProblems(
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc 或 desc") @RequestParam(defaultValue = "desc") String sortOrder,
            HttpServletRequest request) {
        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPageNum(pageNum);
            pageRequest.setPageSize(pageSize);
            pageRequest.setSortField(sortField);
            pageRequest.setSortOrder(sortOrder);
            
            PageResponse<QueryAllProblemResponse> result = problemService.queryAllProblems(pageRequest);
            return Result.success(result);
        } catch (BusinessException e) {
            log.error("查询所有题目异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询所有题目异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/search")
    @Operation(summary = "搜索题目", description = "根据题目名称或题号搜索题目")
    public Result<PageResponse<QueryAllProblemResponse>> searchProblems(
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "题目名称") @RequestBody SearchProblemsRequest searchProblemsRequest,
            HttpServletRequest request) {
        try {
            PageResponse<QueryAllProblemResponse> result = problemService.searchProblems(pageNum, pageSize, searchProblemsRequest);
            return Result.success(result);
        } catch (BusinessException e) {
            log.error("搜索题目异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("搜索题目异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/get/{problemId}")
    @Operation(summary = "查询题目详情", description = "根据题目 ID 查询题目详情")
    @SaCheckLogin
    public Result<BaseProblemInfo> getProblemDetail(
            @Parameter(description = "题目 ID") @PathVariable Long problemId,
            HttpServletRequest request) {
        try {
            BaseProblemInfo result = problemService.getProblemById(problemId);
            return Result.success(result);
        } catch (BusinessException e) {
            log.error("查询题目详情异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询题目详情异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @DeleteMapping("/delete/{problemId}")
    @Operation(summary = "删除题目", description = "管理员或教师删除指定题目，并清理题目标签关联")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Boolean> deleteProblem(
            @Parameter(description = "题目 ID", required = true) @PathVariable Long problemId,
            HttpServletRequest request) {
        try {
            boolean result = problemService.deleteProblem(problemId);
            return Result.success(result, "题目删除成功!");
        } catch (BusinessException e) {
            log.error("删除题目异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("删除题目异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除题目异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/getListProblemsByTagId/{tagId}")
    @Operation(summary = "根据标签 ID 查询所有关联题目", description = "查询指定标签下的所有题目列表")
    public Result<List<QueryAllProblemResponse>> listProblemsByTagId(
            @Parameter(description = "标签 ID", required = true)
            @PathVariable Long tagId,
            HttpServletRequest request) {
        try {
            List<QueryAllProblemResponse> problems = problemService.listProblemsByTagId(tagId);
            return Result.success(problems);
        } catch (BusinessException e) {
            log.error("根据标签 ID 查询题目异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("根据标签 ID 查询题目异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("根据标签 ID 查询题目异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}
