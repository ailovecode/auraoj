package com.zhy.auraojbackend.service.impl;

import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.config.MinioConfig;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.service.MinioService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/9
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public boolean createBucketIfAbsent(String bucketName) {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                // 1. 创建 Bucket
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("成功创建 MinIO Bucket: {}", bucketName);
    
                // 2. 设置 Bucket 权限为 Public Read (允许匿名访问下载)
                String policyJson = """
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                            {
                              "Effect": "Allow",
                              "Principal": "*",
                              "Action": "s3:GetObject",
                              "Resource": "arn:aws:s3:::%s/*"
                            }
                          ]
                        }
                        """.formatted(bucketName);
    
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policyJson)
                                .build()
                );
                log.info("成功设置 Bucket [{}] 为公共读权限", bucketName);
            }
            return true;
        } catch (Exception e) {
            log.error("检查或创建 Bucket 失败，BucketName: {}", bucketName, e);
            throw new BusinessException(ErrorCode.BUCKET_CREATE_FAILED, "创建存储桶失败：" + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.BAD_PARAMS, "上传文件不能为空");
            
        // 确保 Bucket 存在且为 public
        boolean bucketIfAbsent = createBucketIfAbsent(minioConfig.getBucketName());
        ThrowUtils.throwIf(!bucketIfAbsent, ErrorCode.BUCKET_CREATE_FAILED, "Bucket 不存在或未授权");

        // 获取文件原始后缀名 (如 .png, .jpg)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 获取当前年月，格式化为 yyyy/MM 形式，例如：2026/03
        String monthPath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 使用 UUID 防止文件名冲突，加上日期目录按月归档：如 2026/03/uuid.png
        String objectName = monthPath + "/" + UUID.randomUUID().toString().replace("-", "") + extension;
    
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            // 保持原有的 MIME 类型，让浏览器直接渲染而不是下载
                            .contentType(file.getContentType()) 
                            .build()
            );
            return objectName;
        } catch (Exception e) {
            log.error("文件上传到 MinIO 失败，文件名：{}", originalFilename, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean removeFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("从 MinIO 删除文件失败，objectName: {}", objectName, e);
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, "删除文件失败：" + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String objectName) {
        ThrowUtils.throwIf(objectName == null || objectName.isBlank(), ErrorCode.BAD_PARAMS, "文件名不能为空");
        
        String endpoint = minioConfig.getEndpoint();
        // 确保 endpoint 末尾没有多余的斜杠
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        return endpoint + "/" + minioConfig.getBucketName() + "/" + objectName;
    }
}