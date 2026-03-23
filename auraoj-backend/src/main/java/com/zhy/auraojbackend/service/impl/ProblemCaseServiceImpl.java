package com.zhy.auraojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.config.OjConfig;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.mapper.ProblemCaseMapper;
import com.zhy.auraojbackend.model.dto.problemcase.request.SingleProblemCaseUploadRequest;
import com.zhy.auraojbackend.model.dto.problemcase.response.ProblemCaseDeleteResponse;
import com.zhy.auraojbackend.model.entity.Problem;
import com.zhy.auraojbackend.model.entity.ProblemCase;
import com.zhy.auraojbackend.service.ProblemCaseService;
import com.zhy.auraojbackend.service.ProblemService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 测试数据文件服务实现
 *
 * @author zhy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProblemCaseServiceImpl extends ServiceImpl<ProblemCaseMapper, ProblemCase>
    implements ProblemCaseService {

    private static final String INPUT_SUFFIX = ".in";
    private static final String OUTPUT_SUFFIX = ".out";

    private final OjConfig ojConfig;

    @Resource
    @Lazy
    private ProblemService problemService;

    @Override
    public List<ProblemCase> listByProblemId(Long problemId) {
        validateProblemId(problemId);
        LambdaQueryWrapper<ProblemCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProblemCase::getProblemId, problemId)
                .orderByAsc(ProblemCase::getGmtCreate)
                .orderByAsc(ProblemCase::getId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ProblemCase> uploadZip(Long problemId, MultipartFile zipFile) {
        validateProblemId(problemId);
        validateZipFile(zipFile);
        Path problemDir = ensureProblemDir(problemId);

        Map<String, byte[]> fileContentMap = readZipEntries(zipFile);
        ThrowUtils.throwIf(fileContentMap.isEmpty(), ErrorCode.BAD_PARAMS, "zip 包中未找到有效的 .in/.out 文件");

        Map<String, PairFileHolder> pairMap = buildPairMap(fileContentMap);
        List<ProblemCase> existingCases = listByProblemId(problemId);
        Map<String, ProblemCase> inputCaseMap = new LinkedHashMap<>();
        Map<String, ProblemCase> outputCaseMap = new LinkedHashMap<>();
        for (ProblemCase problemCase : existingCases) {
            if (StringUtils.isNotBlank(problemCase.getInputFile())) {
                inputCaseMap.put(problemCase.getInputFile(), problemCase);
            }
            if (StringUtils.isNotBlank(problemCase.getOutputFile())) {
                outputCaseMap.put(problemCase.getOutputFile(), problemCase);
            }
        }

        List<ProblemCase> savedCases = new ArrayList<>();
        for (PairFileHolder holder : pairMap.values()) {
            writeFile(problemDir.resolve(holder.getInputFileName()), fileContentMap.get(holder.getInputFileName()));
            writeFile(problemDir.resolve(holder.getOutputFileName()), fileContentMap.get(holder.getOutputFileName()));

            ProblemCase problemCase = resolveExistingCase(inputCaseMap, outputCaseMap, holder);
            if (problemCase == null) {
                problemCase = new ProblemCase();
                problemCase.setProblemId(problemId);
            }
            problemCase.setInputFile(holder.getInputFileName());
            problemCase.setOutputFile(holder.getOutputFileName());
            problemCase.setInputFileSize((long) fileContentMap.get(holder.getInputFileName()).length);
            problemCase.setOutputFileSize((long) fileContentMap.get(holder.getOutputFileName()).length);
            problemCase.setStatus(1);
            boolean saved = this.saveOrUpdate(problemCase);
            ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "保存测试数据记录失败");
            savedCases.add(problemCase);
        }

        savedCases.sort(Comparator.comparing(ProblemCase::getId, Comparator.nullsLast(Long::compareTo)));
        return savedCases;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProblemCase uploadSingleCase(SingleProblemCaseUploadRequest request) {
        Long problemId = request.getProblemId();
        String inputContent = request.getInputContent();
        String outputContent = request.getOutputContent();
        String fileName = request.getFileName();

        validateProblemId(problemId);
        ThrowUtils.throwIf(StringUtils.isBlank(inputContent), ErrorCode.BAD_PARAMS, "输入内容不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(outputContent), ErrorCode.BAD_PARAMS, "输出内容不能为空");

        Path problemDir = ensureProblemDir(problemId);
        byte[] inputBytes = inputContent.getBytes(StandardCharsets.UTF_8);
        byte[] outputBytes = outputContent.getBytes(StandardCharsets.UTF_8);
        String inputFileName = fileName + INPUT_SUFFIX;
        String outputFileName = fileName + OUTPUT_SUFFIX;

        writeFile(problemDir.resolve(inputFileName), inputBytes);
        writeFile(problemDir.resolve(outputFileName), outputBytes);

        ProblemCase problemCase = new ProblemCase();
        problemCase.setProblemId(problemId);
        problemCase.setInputFile(inputFileName);
        problemCase.setOutputFile(outputFileName);
        problemCase.setInputFileSize((long) inputBytes.length);
        problemCase.setOutputFileSize((long) outputBytes.length);
        problemCase.setStatus(1);
        boolean saved = this.save(problemCase);
        ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "保存测试数据记录失败");
        return problemCase;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProblemCaseDeleteResponse deleteCaseFile(Long problemId, String fileName) {
        validateProblemId(problemId);
        String safeFileName = normalizeFileName(fileName);
        ProblemCase problemCase = findCaseByFileName(problemId, safeFileName);
        ThrowUtils.throwIf(problemCase == null, ErrorCode.RESOURCE_NOT_FOUND, "未找到对应的测试数据文件记录");

        // 1. 删除物理文件
        deletePhysicalFileIfExists(buildProblemDir(problemId).resolve(safeFileName));

        String pairedFileName;
        boolean isInputFile = safeFileName.equals(problemCase.getInputFile());

        // 2. 判断当前删除的是输入还是输出文件，并记录配对文件名
        if (isInputFile) {
            pairedFileName = problemCase.getOutputFile();
            problemCase.setInputFile(null);
            problemCase.setInputFileSize(null);
        } else {
            pairedFileName = problemCase.getInputFile();
            problemCase.setOutputFile(null);
            problemCase.setOutputFileSize(null);
        }

        // 3. 核心修复：更新数据库记录
        if (StringUtils.isBlank(problemCase.getInputFile()) && StringUtils.isBlank(problemCase.getOutputFile())) {
            // 如果输入和输出文件都为空了，直接删除整条记录
            boolean removed = this.removeById(problemCase.getId());
            ThrowUtils.throwIf(!removed, ErrorCode.SYSTEM_ERROR, "删除测试数据记录失败");
        } else {
            // 使用 LambdaUpdateWrapper 显式将字段更新为 null
            LambdaUpdateWrapper<ProblemCase> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProblemCase::getId, problemCase.getId());

            if (isInputFile) {
                // 如果删除的是输入文件，强制将 input 相关的字段置为 null
                updateWrapper.set(ProblemCase::getInputFile, null)
                        .set(ProblemCase::getInputFileSize, null);
            } else {
                // 如果删除的是输出文件，强制将 output 相关的字段置为 null
                updateWrapper.set(ProblemCase::getOutputFile, null)
                        .set(ProblemCase::getOutputFileSize, null);
            }

            // 注意：第一个参数传 null，完全交给 updateWrapper 去生成包含 null 的 SET 语句
            boolean updated = this.update(null, updateWrapper);
            ThrowUtils.throwIf(!updated, ErrorCode.SYSTEM_ERROR, "更新测试数据记录失败");
        }

        // 4. 构造响应
        String reminder = StringUtils.isNotBlank(pairedFileName)
                ? "已删除 " + safeFileName + "，请确认是否需要同时删除配对文件 " + pairedFileName
                : "已删除 " + safeFileName;

        return ProblemCaseDeleteResponse.builder()
                .deleted(Boolean.TRUE)
                .deletedFileName(safeFileName)
                .pairedFileName(pairedFileName)
                .reminder(reminder)
                .build();
    }

    @Override
    public org.springframework.core.io.Resource loadCaseFileAsResource(Long problemId, String fileName) {
        validateProblemId(problemId);
        String safeFileName = normalizeFileName(fileName);
        ProblemCase problemCase = findCaseByFileName(problemId, safeFileName);
        ThrowUtils.throwIf(problemCase == null, ErrorCode.RESOURCE_NOT_FOUND, "未找到对应的测试数据文件记录");

        Path filePath = buildProblemDir(problemId).resolve(safeFileName);
        ThrowUtils.throwIf(!Files.exists(filePath), ErrorCode.RESOURCE_NOT_FOUND, "测试数据文件不存在");
        return new FileSystemResource(filePath);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProblemCase renameCaseFile(Long problemId, String oldFileName, String newFileName) {
        validateProblemId(problemId);
        String safeOldFileName = normalizeFileName(oldFileName);
        String safeNewFileName = normalizeFileName(newFileName);

        ThrowUtils.throwIf(StringUtils.isBlank(safeOldFileName), ErrorCode.BAD_PARAMS, "原文件名不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(safeNewFileName), ErrorCode.BAD_PARAMS, "新文件名不能为空");
        ThrowUtils.throwIf(safeOldFileName.equals(safeNewFileName), ErrorCode.BAD_PARAMS, "新旧文件名相同，无需重命名");
        validateCaseFileName(safeNewFileName);

        ProblemCase problemCase = findCaseByFileName(problemId, safeOldFileName);
        ThrowUtils.throwIf(problemCase == null, ErrorCode.RESOURCE_NOT_FOUND, "未找到对应的测试数据文件记录");

        Path problemDir = ensureProblemDir(problemId);
        Path oldFilePath = problemDir.resolve(safeOldFileName);
        Path newFilePath = problemDir.resolve(safeNewFileName);

        ThrowUtils.throwIf(!Files.exists(oldFilePath), ErrorCode.RESOURCE_NOT_FOUND, "原文件不存在");
        ThrowUtils.throwIf(Files.exists(newFilePath), ErrorCode.FILE_RENAME_FAILED, "新文件名已存在");

        try {
            Files.move(oldFilePath, newFilePath);
        } catch (IOException e) {
            log.error("重命名测试数据文件失败：{} -> {}", safeOldFileName, safeNewFileName, e);
            throw new BusinessException(ErrorCode.FILE_RENAME_FAILED, "重命名文件失败：" + e.getMessage());
        }

        if (safeOldFileName.equals(problemCase.getInputFile())) {
            problemCase.setInputFile(safeNewFileName);
        } else if (safeOldFileName.equals(problemCase.getOutputFile())) {
            problemCase.setOutputFile(safeNewFileName);
        }

        boolean updated = this.updateById(problemCase);
        ThrowUtils.throwIf(!updated, ErrorCode.SYSTEM_ERROR, "更新测试数据记录失败");

        return problemCase;
    }

    private void validateProblemId(Long problemId) {
        ThrowUtils.throwIf(problemId == null || problemId <= 0, ErrorCode.BAD_PARAMS, "题目 ID 不合法");
        Problem problem = problemService.getById(problemId);
        ThrowUtils.throwIf(problem == null, ErrorCode.RESOURCE_NO_PROBLEM, "题目不存在");
    }

    private void validateZipFile(MultipartFile zipFile) {
        ThrowUtils.throwIf(zipFile == null || zipFile.isEmpty(), ErrorCode.BAD_PARAMS, "请上传 zip 文件");
        String originalFilename = zipFile.getOriginalFilename();
        ThrowUtils.throwIf(StringUtils.isBlank(originalFilename)
                || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".zip"), ErrorCode.BAD_PARAMS, "仅支持上传 zip 文件");
    }

    private Map<String, byte[]> readZipEntries(MultipartFile zipFile) {
        Map<String, byte[]> fileContentMap = new LinkedHashMap<>();
        try (InputStream inputStream = zipFile.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String fileName = normalizeFileName(entry.getName());
                validateCaseFileName(fileName);
                ThrowUtils.throwIf(fileContentMap.containsKey(fileName), ErrorCode.BAD_PARAMS, "zip 包中存在重复文件: " + fileName);
                fileContentMap.put(fileName, readAllBytes(zipInputStream));
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取 zip 文件失败: " + e.getMessage());
        }
        return fileContentMap;
    }

    private Map<String, PairFileHolder> buildPairMap(Map<String, byte[]> fileContentMap) {
        Map<String, PairFileHolder> pairMap = new LinkedHashMap<>();
        for (String fileName : fileContentMap.keySet()) {
            String baseName = getBaseName(fileName);
            PairFileHolder holder = pairMap.computeIfAbsent(baseName, key -> new PairFileHolder());
            if (isInputFile(fileName)) {
                holder.setInputFileName(fileName);
            } else {
                holder.setOutputFileName(fileName);
            }
        }
        for (Map.Entry<String, PairFileHolder> entry : pairMap.entrySet()) {
            PairFileHolder holder = entry.getValue();
            ThrowUtils.throwIf(StringUtils.isAnyBlank(holder.getInputFileName(), holder.getOutputFileName()),
                    ErrorCode.BAD_PARAMS,
                    "zip 包中的测试数据必须成对出现，缺少配对文件: " + entry.getKey());
        }
        return pairMap;
    }

    private ProblemCase resolveExistingCase(Map<String, ProblemCase> inputCaseMap,
                                            Map<String, ProblemCase> outputCaseMap,
                                            PairFileHolder holder) {
        ProblemCase byInput = inputCaseMap.get(holder.getInputFileName());
        if (byInput != null) {
            return byInput;
        }
        return outputCaseMap.get(holder.getOutputFileName());
    }

    private ProblemCase findCaseByFileName(Long problemId, String fileName) {
        LambdaQueryWrapper<ProblemCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProblemCase::getProblemId, problemId)
                .and(wrapper -> wrapper.eq(ProblemCase::getInputFile, fileName)
                        .or()
                        .eq(ProblemCase::getOutputFile, fileName))
                .last("limit 1");
        return this.getOne(queryWrapper, false);
    }

    private String normalizeFileName(String fileName) {
        ThrowUtils.throwIf(StringUtils.isBlank(fileName), ErrorCode.BAD_PARAMS, "文件名不能为空");
        String normalized = Paths.get(fileName).getFileName().toString();
        ThrowUtils.throwIf(StringUtils.isBlank(normalized) || normalized.contains(".."), ErrorCode.BAD_PARAMS, "文件名不合法");
        return normalized;
    }

    private void validateCaseFileName(String fileName) {
        ThrowUtils.throwIf(!(isInputFile(fileName) || isOutputFile(fileName)),
                ErrorCode.BAD_PARAMS,
                "测试数据文件仅支持 .in/.out 格式: " + fileName);
    }

    private boolean isInputFile(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).endsWith(INPUT_SUFFIX);
    }

    private boolean isOutputFile(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).endsWith(OUTPUT_SUFFIX);
    }

    private String getBaseName(String fileName) {
        if (isInputFile(fileName)) {
            return fileName.substring(0, fileName.length() - INPUT_SUFFIX.length());
        }
        if (isOutputFile(fileName)) {
            return fileName.substring(0, fileName.length() - OUTPUT_SUFFIX.length());
        }
        throw new BusinessException(ErrorCode.BAD_PARAMS, "不支持的测试数据文件名: " + fileName);
    }

    private Path ensureProblemDir(Long problemId) {
        Path problemDir = buildProblemDir(problemId);
        try {
            Files.createDirectories(problemDir);
            return problemDir;
        } catch (IOException e) {
            log.error("创建题目测试数据目录失败: {}", problemDir, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建题目测试数据目录失败: " + e.getMessage());
        }
    }

    private Path buildProblemDir(Long problemId) {
        return Paths.get(ojConfig.getPathPrefix(), String.valueOf(problemId));
    }

    private void writeFile(Path targetPath, byte[] content) {
        try {
            Files.write(targetPath, content);
        } catch (IOException e) {
            log.error("写入测试数据文件失败: {}", targetPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "写入测试数据文件失败: " + targetPath.getFileName());
        }
    }

    private void deletePhysicalFileIfExists(Path targetPath) {
        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            log.error("删除测试数据文件失败: {}", targetPath, e);
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, "删除测试数据文件失败: " + targetPath.getFileName());
        }
    }

    private byte[] readAllBytes(ZipInputStream zipInputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = zipInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    private static class PairFileHolder {
        private String inputFileName;
        private String outputFileName;

        public String getInputFileName() {
            return inputFileName;
        }

        public void setInputFileName(String inputFileName) {
            this.inputFileName = inputFileName;
        }

        public String getOutputFileName() {
            return outputFileName;
        }

        public void setOutputFileName(String outputFileName) {
            this.outputFileName = outputFileName;
        }
    }
}