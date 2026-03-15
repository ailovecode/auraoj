package com.zhy.auraojbackend.service;

/**
 * 题目文件服务
 * @author zhy
 * @Date 2026/3/15
 */
public interface ProblemFileService {

    /**
     * 保存样例输入输出文件
     * @param problemId 题目 ID
     * @param sampleInput 样例输入内容
     * @param sampleOutput 样例输出内容
     */
    void saveSampleFiles(Long problemId, String sampleInput, String sampleOutput);

    /**
     * 获取样例输入文件路径
     * @param problemId 题目 ID
     * @return 样例输入文件路径
     */
    String getSampleInputPath(Long problemId);

    /**
     * 获取样例输出文件路径
     * @param problemId 题目 ID
     * @return 样例输出文件路径
     */
    String getSampleOutputPath(Long problemId);
}
