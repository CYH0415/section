package com.zju.section.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.zju.section.common.ApiResult;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理数据访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResult<?>> handleDataAccessException(DataAccessException e) {
        ApiResult<?> result = ApiResult.error("数据库操作异常: " + e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<?>> handleException(Exception e) {
        ApiResult<?> result = ApiResult.error("系统异常: " + e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}