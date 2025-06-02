package com.zju.main.section.common;

/**
 * 统一API响应结果封装
 */
public class ApiResult<T> {
    private Integer code;
    private String message;
    private T data;

    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String ERROR_MESSAGE = "操作失败";

    public ApiResult() {
    }

    public ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    public static <T> ApiResult<T> success(String message) {
        return new ApiResult<>(SUCCESS_CODE, message, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(SUCCESS_CODE, message, data);
    }

    public static <T> ApiResult<T> error() {
        return new ApiResult<>(ERROR_CODE, ERROR_MESSAGE, null);
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(ERROR_CODE, message, null);
    }

    public static <T> ApiResult<T> error(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static ApiResult<?> failure(String message) {
        return new ApiResult<>(ERROR_CODE, message, null);
    }
        


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}