package com.zhy.auraojbackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/1 11:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private int code;

    @SuppressWarnings("java:S1948")
    private T data;

    private String message;

    public Result(int code, T data) {
        this(code, data, "");
    }

    public Result(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

    /**
     * 成功
     *
     * @param data 返回的数据
     * @param <T>  数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), data, "ok");
    }

    /**
     * 成功
     *
     * @param data 返回的数据
     * @param <T>  数据类型
     * @return Result<T>
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), data, message);
    }

    /**
     * 失败 (通过 ErrorCode 枚举)
     *
     * @param errorCode 错误码枚举
     * @param <T>       数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode);
    }

    /**
     * 失败 (自定义状态码和错误信息)
     *
     * @param code    错误状态码
     * @param message 自定义错误信息
     * @param <T>     数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, null, message);
    }

    /**
     * 失败 (覆盖 ErrorCode 的默认 message)
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误信息
     * @param <T>       数据类型
     * @return Result<T>
     */
    public static <T> Result<T> error(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), null, message);
    }
}
