package com.zhy.auraojbackend.service.impl;

import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.config.OjConfig;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.service.ProblemFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * 题目文件服务实现
 * @author zhy
 * @Date 2026/3/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemFileServiceImpl implements ProblemFileService {

    private final OjConfig ojConfig;

    /**
     * 输入文件名
     */
    private static final String INPUT_FILE_NAME = "%s.in";

    /**
     * 输出文件名
     */
    private static final String OUTPUT_FILE_NAME = "%s.out";

    @Override
    public void saveSampleFiles(Long problemId, String sampleInput, String sampleOutput) {
        // 构建题目目录路径
        String problemDirPath = buildProblemDirPath(problemId);
        
        // 创建目录（如果不存在）
        File problemDir = new File(problemDirPath);
        if (!problemDir.exists()) {
            boolean mkdirResult = problemDir.mkdirs();
            ThrowUtils.throwIf(!mkdirResult, ErrorCode.SYSTEM_ERROR, 
                    "创建题目目录失败：" + problemDirPath);
            log.info("创建题目目录成功：{}", problemDirPath);
        }

        // 保存样例输入文件
        if (sampleInput != null && !sampleInput.trim().isEmpty()) {
            String inputFilePath = getSampleInputPath(problemId);
            saveToFile(inputFilePath, sampleInput);
            log.info("保存样例输入文件成功，路径：{}", inputFilePath);
        }

        // 保存样例输出文件
        if (sampleOutput != null && !sampleOutput.trim().isEmpty()) {
            String outputFilePath = getSampleOutputPath(problemId);
            saveToFile(outputFilePath, sampleOutput);
            log.info("保存样例输出文件成功，路径：{}", outputFilePath);
        }
    }

    @Override
    public String getSampleInputPath(Long problemId) {
        // 使用 Paths.get 自动处理路径分隔符
        return Paths.get(buildProblemDirPath(problemId), String.format(INPUT_FILE_NAME, "sample_input")).toString();
    }

    @Override
    public String getSampleOutputPath(Long problemId) {
        // 使用 Paths.get 自动处理路径分隔符
        return Paths.get(buildProblemDirPath(problemId), String.format(OUTPUT_FILE_NAME, "sample_output")).toString();
    }

    /**
     * 构建题目目录路径
     * @param problemId 题目 ID
     * @return 题目目录完整路径
     */
    private String buildProblemDirPath(Long problemId) {
        // Windows: G:\judge\data\123
        // Linux: /home/judge/data/123
        String pathPrefix = ojConfig.getPathPrefix();
        
        // 使用 Paths.get 自动处理路径分隔符，无需手动替换
        return Paths.get(pathPrefix, problemId.toString()).toString();
    }

    /**
     * 将内容保存到文件
     * @param filePath 文件路径
     * @param content 文件内容
     */
    private void saveToFile(String filePath, String content) {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            log.error("保存文件失败，路径：{}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, 
                    "保存文件失败：" + e.getMessage());
        }
    }
}
