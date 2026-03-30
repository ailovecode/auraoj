package com.zhy.auraojbackend.common;

import lombok.Getter;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/1
 */
@Getter
public enum ErrorCode {

    // --- 成功 ---
    SUCCESS(200, "操作成功"),

    // --- 客户端错误 (4xxxx) ---
    // 400xx: 参数类
    BAD_PARAMS(40001, "请求参数错误"),
    MISSING_PARAM(40002, "缺少必要参数"),

    // 401xx: 认证类
    NO_LOGIN(40100, "用户未登录"),
    TOKEN_EXPIRED(40101, "登录凭证已过期"),

    // 403xx: 授权类
    ACCESS_DENIED(40300, "无权访问该资源"),
    NO_AUTHORITY_ALTER(40301, "无权修改该资源"),

    // 404xx: 资源类
    RESOURCE_NOT_FOUND(40400, "资源不存在"),
    RESOURCE_NO_USERINFO(40401, "用户信息不存在"),
    RESOURCE_NO_TAG(40402, "标签不存在"),
    RESOURCE_NO_PROBLEM(40403, "未找到错误信息"),

    // --- 服务端错误 (5xxxx) ---
    // 500xx: 系统类
    SYSTEM_ERROR(50000, "服务器内部错误"),

    // 501xx: 第三方/依赖类
    REMOTE_CALL_FAILED(50100, "远程服务调用失败"),

    // 502xx: 业务特定类 (如判题系统)
    CODE_COMPILE_ERROR(50201, "代码编译失败"),
    CODE_RUNTIME_ERROR(50202, "代码运行异常"),
    CODE_TIME_LIMIT_EXCEEDED(50203, "代码执行超时"),

    // 503xx: MinIO 文件存储类
    FILE_UPLOAD_FAILED(50300, "文件上传失败"),
    FILE_DELETE_FAILED(50301, "文件删除失败"),
    BUCKET_CREATE_FAILED(50302, "存储桶创建失败"),
    UPLOAD_AVATAR_ERROR(50303, "上传头像失败"),
    FILE_RENAME_FAILED(50304, "文件重命名失败"),

    // 504xx: 题目评测
    TOO_MANY_REQUESTS(50400, "请求过于频繁");
    
    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
