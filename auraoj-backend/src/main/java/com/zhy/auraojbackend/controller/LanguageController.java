package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.language.request.LanguageAddRequest;
import com.zhy.auraojbackend.model.dto.language.request.LanguageQueryRequest;
import com.zhy.auraojbackend.model.dto.language.request.LanguageUpdateRequest;
import com.zhy.auraojbackend.model.dto.language.response.LanguageResponse;
import com.zhy.auraojbackend.model.entity.Language;
import com.zhy.auraojbackend.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/4/11
 */
@RestController
@RequestMapping("/api/language")
@Tag(name = "代码语言管理", description = "代码语言管理")
@Slf4j
public class LanguageController {

    @Resource
    private LanguageService languageService;

    @PostMapping("/add")
    @Operation(summary = "新增代码语言", description = "管理员或教师新增代码语言")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Long> addLanguage(
            @Parameter(description = "新增代码语言请求参数") @RequestBody LanguageAddRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long languageId = languageService.addLanguage(request);
            return Result.success(languageId, "代码语言添加成功!");
        } catch (BusinessException e) {
            log.error("添加代码语言异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("添加代码语言异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("添加代码语言异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新代码语言", description = "管理员或教师更新代码语言信息")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Language> updateLanguage(
            @Parameter(description = "更新代码语言请求参数") @RequestBody LanguageUpdateRequest request,
            HttpServletRequest httpRequest) {
        try {
            Language language = languageService.updateLanguage(request);
            return Result.success(language, "代码语言更新成功!");
        } catch (BusinessException e) {
            log.error("更新代码语言异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("更新代码语言异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新代码语言异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除代码语言", description = "管理员或教师删除指定代码语言")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<Boolean> deleteLanguage(
            @Parameter(description = "语言 ID", required = true) @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            boolean result = languageService.deleteLanguage(id);
            return Result.success(result, "代码语言删除成功!");
        } catch (BusinessException e) {
            log.error("删除代码语言异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("删除代码语言异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除代码语言异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/list")
    @Operation(summary = "分页查询代码语言列表", description = "获取代码语言列表（支持分页和按名称筛选）")
    @SaCheckLogin
    public Result<PageResponse<LanguageResponse>> listLanguages(
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc 或 desc") @RequestParam(defaultValue = "asc") String sortOrder,
            @Parameter(description = "语言名称（模糊查询）") @RequestParam(required = false) String name,
            HttpServletRequest httpRequest) {
        try {
            LanguageQueryRequest request = new LanguageQueryRequest();
            request.setPageNum(pageNum);
            request.setPageSize(pageSize);
            request.setSortField(sortField);
            request.setSortOrder(sortOrder);
            request.setName(name);
            
            PageResponse<LanguageResponse> result = languageService.listLanguages(request);
            return Result.success(result);
        } catch (BusinessException e) {
            log.error("查询代码语言列表异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询代码语言列表异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "查询代码语言详情", description = "根据 ID 获取单个代码语言的详细信息")
    @SaCheckLogin
    public Result<LanguageResponse> getLanguageById(
            @Parameter(description = "语言 ID", required = true) @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            LanguageResponse language = languageService.getLanguageById(id);
            return Result.success(language);
        } catch (BusinessException e) {
            log.error("查询代码语言详情异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("查询代码语言详情异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询代码语言详情异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}
