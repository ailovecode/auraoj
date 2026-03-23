package com.zhy.auraojbackend.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.config.OjConfig;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.model.entity.ProblemCase;
import com.zhy.auraojbackend.service.ProblemCaseService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/22
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FileManager {

    @Resource
    private ProblemCaseService problemCaseService;

    private final OjConfig ojConfig;
    private static final String INPUT_FILE_NAME = "sample_test.in";
    private static final String OUTPUT_FILE_NAME = "sample_test.out";


    /**
     * 保存文件
     * @param problemId
     * @param sampleInput
     * @param sampleOutput
     */
    public void saveSampleFiles(Long problemId, String sampleInput, String sampleOutput) {
        String problemDirPath = buildProblemDirPath(problemId);

        LambdaQueryWrapper<ProblemCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProblemCase::getProblemId, problemId)
                .and(wrapper -> wrapper.eq(ProblemCase::getInputFile, INPUT_FILE_NAME)
                        .or()
                        .eq(ProblemCase::getOutputFile, OUTPUT_FILE_NAME))
                .last("limit 1");
        ProblemCase problemCase = problemCaseService.getOne(queryWrapper, false);
        if (problemCase == null) {
            problemCase = new ProblemCase();
            problemCase.setProblemId(problemId);
        }

        File problemDir = new File(problemDirPath);
        if (!problemDir.exists()) {
            boolean mkdirResult = problemDir.mkdirs();
            ThrowUtils.throwIf(!mkdirResult, ErrorCode.SYSTEM_ERROR, "create sample dir failed: " + problemDirPath);
            log.info("create sample dir success: {}", problemDirPath);
        }

        if (sampleInput != null && !sampleInput.trim().isEmpty()) {
            String inputFilePath = getSampleInputPath(problemId);
            saveToFile(inputFilePath, sampleInput);
            File inputFile = new File(inputFilePath);
            problemCase.setInputFile(INPUT_FILE_NAME);
            problemCase.setInputFileSize(inputFile.length());
            log.info("save sample input success: {}, size: {}", inputFilePath, inputFile.length());
        }

        if (sampleOutput != null && !sampleOutput.trim().isEmpty()) {
            String outputFilePath = getSampleOutputPath(problemId);
            saveToFile(outputFilePath, sampleOutput);
            File outputFile = new File(outputFilePath);
            problemCase.setOutputFile(OUTPUT_FILE_NAME);
            problemCase.setOutputFileSize(outputFile.length());
            log.info("save sample output success: {}, size: {}", outputFilePath, outputFile.length());
        }

        problemCase.setStatus(1);
        boolean saved = problemCase.getId() == null ?
                problemCaseService.save(problemCase) : problemCaseService.updateById(problemCase);
        ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "save sample file record failed");
    }

    public String getSampleInputPath(Long problemId) {
        return Paths.get(buildProblemDirPath(problemId), INPUT_FILE_NAME).toString();
    }

    public String getSampleOutputPath(Long problemId) {
        return Paths.get(buildProblemDirPath(problemId), OUTPUT_FILE_NAME).toString();
    }

    private String buildProblemDirPath(Long problemId) {
        return Paths.get(ojConfig.getPathPrefix(), problemId.toString()).toString();
    }

    private void saveToFile(String filePath, String content) {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            log.error("save file failed: {}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "save file failed: " + e.getMessage());
        }
    }
}