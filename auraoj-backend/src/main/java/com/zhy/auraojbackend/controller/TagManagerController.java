package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.tag.request.TagAddRequest;
import com.zhy.auraojbackend.model.dto.tag.request.TagUpdateRequest;
import com.zhy.auraojbackend.model.entity.TagInfo;
import com.zhy.auraojbackend.service.TagService;
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
@RequestMapping("/api/tag")
@Tag(name = "标签管理", description = "标签管理接口")
@Slf4j
public class TagManagerController {

    @Resource
    private TagService tagService;

    @GetMapping("/list")
    @Operation(summary = "查询所有标签", description = "获取系统中所有标签列表，支持按标签名模糊查询")
    @SaCheckLogin
    public Result<List<TagInfo>> listAllTags(
            @Parameter(description = "标签名（支持模糊查询）") 
            @RequestParam(required = false) String tagName,
            HttpServletRequest request) {
        try {
            List<TagInfo> tags = tagService.listAllTags(tagName);
            return Result.success(tags);
        } catch (BusinessException e) {
            log.error("查询所有标签异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询所有标签异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/add")
    @Operation(summary = "新增标签", description = "新增单个标签")
    @SaCheckLogin
    public Result<Long> addTag(
            @Parameter(description = "新增标签请求参数") 
            @RequestBody TagAddRequest tagAddRequest,
            HttpServletRequest request) {
        try {
            Long tagId = tagService.addTag(tagAddRequest);
            return Result.success(tagId, "标签添加成功!");
        } catch (BusinessException e) {
            log.error("添加标签异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("添加标签异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("添加标签异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "更新标签", description = "更新指定标签信息")
    @SaCheckLogin
    public Result<TagInfo> updateTag(
            @Parameter(description = "更新标签请求参数") 
            @RequestBody TagUpdateRequest tagUpdateRequest,
            HttpServletRequest request) {
        try {
            TagInfo result = tagService.updateTag(tagUpdateRequest);
            return Result.success(result, "标签更新成功!");
        } catch (BusinessException e) {
            log.error("更新标签异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("更新标签异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新标签异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除标签", description = "删除指定标签")
    @SaCheckLogin
    public Result<Boolean> deleteTag(
            @Parameter(description = "标签 ID", required = true) 
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            boolean result = tagService.deleteTag(id);
            return Result.success(result, "标签删除成功!");
        } catch (BusinessException e) {
            log.error("删除标签异常 - 业务异常", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("删除标签异常 - 参数校验失败", e);
            return Result.error(ErrorCode.BAD_PARAMS.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除标签异常 - 系统异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}
