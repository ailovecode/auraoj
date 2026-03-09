package com.zhy.auraojbackend.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/9
 */
public interface MinioService {
    /**
     * 检查并创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return 是否创建成功
     */
    boolean createBucketIfAbsent(String bucketName);

    /**
     * 上传文件
     * @return 存储在 MinIO 中的文件名
     */
    String uploadFile(MultipartFile file);

    /**
     * 删除文件
     */
    boolean removeFile(String objectName);

    /**
     * 获取文件外链 (预签名 URL)
     * @param objectName 文件名
     */
    String getFileUrl(String objectName);
}
