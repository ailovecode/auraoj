package com.zhy.auraojbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.common.Result;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.model.dto.problemcase.request.DeleteProblemCaseFileRequest;
import com.zhy.auraojbackend.model.dto.problemcase.request.RenameProblemCaseRequest;
import com.zhy.auraojbackend.model.dto.problemcase.request.SingleProblemCaseUploadRequest;
import com.zhy.auraojbackend.model.dto.problemcase.response.ProblemCaseDeleteResponse;
import com.zhy.auraojbackend.model.dto.problemcase.response.ProblemCaseFileResponse;
import com.zhy.auraojbackend.model.entity.ProblemCase;
import com.zhy.auraojbackend.service.ProblemCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhy
 */
@RestController
@RequestMapping("/api/case")
@Tag(name = "测试数据管理", description = "测试数据管理接口")
@Slf4j
public class ProblemCaseController {

    @Resource
    private ProblemCaseService problemCaseService;

    @GetMapping("/list/{problemId}")
    @Operation(summary = "根据题目 ID 获取测试数据文件列表")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<List<ProblemCaseFileResponse>> listByProblemId(
            @Parameter(description = "题目 ID", required = true) @PathVariable Long problemId) {
        try {
            List<ProblemCaseFileResponse> result = problemCaseService.listByProblemId(problemId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
            return Result.success(result);
        } catch (BusinessException e) {
            log.error("查询测试数据文件列表失败", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询测试数据文件列表异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping(value = "/upload/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传 zip 测试数据文件")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<List<ProblemCaseFileResponse>> uploadZip(
            @Parameter(description = "题目 ID", required = true) @RequestParam Long problemId,
            @Parameter(description = "zip 文件", required = true) @RequestPart("file") MultipartFile file) {
        try {
            List<ProblemCaseFileResponse> result = problemCaseService.uploadZip(problemId, file)
                    .stream()
                    .map(this::toResponse)
                    .toList();
            return Result.success(result, "zip 测试数据上传成功");
        } catch (BusinessException e) {
            log.error("上传 zip 测试数据文件失败", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("上传 zip 测试数据文件异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/upload/single")
    @Operation(summary = "上传单个测试数据")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<ProblemCaseFileResponse> uploadSingle(@RequestBody SingleProblemCaseUploadRequest request) {
        try {
            ProblemCase problemCase = problemCaseService.uploadSingleCase(request);
            return Result.success(toResponse(problemCase), "单个测试数据上传成功");
        } catch (BusinessException e) {
            log.error("上传单个测试数据失败", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("上传单个测试数据异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除测试数据文件")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<ProblemCaseDeleteResponse> deleteFile(@RequestBody DeleteProblemCaseFileRequest request) {
        try {
            ProblemCaseDeleteResponse response = problemCaseService.deleteCaseFile(request.getProblemId(), request.getFileName());
            return Result.success(response, response.getReminder());
        } catch (BusinessException e) {
            log.error("删除测试数据文件失败", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除测试数据文件异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @GetMapping("/download")
    @Operation(summary = "下载测试数据文件")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public ResponseEntity<org.springframework.core.io.Resource> download(
            @Parameter(description = "题目 ID", required = true) @RequestParam Long problemId,
            @Parameter(description = "文件名", required = true) @RequestParam String fileName) {
        org.springframework.core.io.Resource resource = problemCaseService.loadCaseFileAsResource(problemId, fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(resource.getFilename(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/rename")
    @Operation(summary = "重命名测试数据文件")
    @SaCheckRole(value = {"teacher", "admin"}, mode = SaMode.OR)
    public Result<ProblemCaseFileResponse> renameFile(@RequestBody RenameProblemCaseRequest request) {
        try {
            ProblemCase problemCase = problemCaseService.renameCaseFile(request.getProblemId(), request.getOldFileName(), request.getNewFileName());
            return Result.success(toResponse(problemCase), "重命名成功，请确认是否需将对应文件重命名！");
        } catch (BusinessException e) {
            log.error("重命名测试数据文件失败", e);
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("重命名测试数据文件异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    private ProblemCaseFileResponse toResponse(ProblemCase problemCase) {
        ProblemCaseFileResponse response = new ProblemCaseFileResponse();
        BeanUtils.copyProperties(problemCase, response);
        return response;
    }
}