package com.techblog.backend.config;

import com.techblog.backend.common.enums.ErrorCode;
import com.techblog.backend.common.exception.BusinessException;
import com.techblog.backend.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理控制器层抛出的各类异常，返回标准化的错误响应
 * 
 * 处理的异常类型：
 * - BusinessException: 自定义业务异常（优先处理）
 * - MethodArgumentNotValidException: 参数校验失败
 * - AuthenticationException: 认证失败（用户名/密码错误）
 * - AccessDeniedException: 权限不足
 * - RuntimeException: 其他运行时异常
 * - Exception: 未预期的系统异常（兜底处理）
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理自定义业务异常
     * @param ex 业务异常对象
     * @param request HTTP 请求对象
     * @return 包含错误码、消息、时间戳和路径的响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        log.warn("Business exception [{}]: {}", ex.getCode(), ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
            ex.getCode(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }
    
    /**
     * 处理参数校验异常（@Valid/@Validated 注解触发）
     * @param ex 校验异常对象
     * @param request HTTP 请求对象
     * @return 包含所有字段错误信息的响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        // 收集所有字段错误
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        log.warn("Validation failed: {}", fieldErrors);
        
        // 构建错误消息（将所有字段错误拼接）
        String message = fieldErrors.values().iterator().next(); // 取第一个错误作为主消息
        
        ErrorResponse errorResponse = ErrorResponse.of(
            ErrorCode.VALIDATION_FAILED.getCode(),
            message,
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 处理认证失败异常（用户名/密码错误、Token 无效等）
     * @param ex 认证异常对象
     * @param request HTTP 请求对象
     * @return 401 Unauthorized 响应
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
            ErrorCode.UNAUTHORIZED.getCode(),
            ErrorCode.UNAUTHORIZED.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * 处理访问拒绝异常（权限不足）
     * @param ex 访问拒绝异常对象
     * @param request HTTP 请求对象
     * @return 403 Forbidden 响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
            ErrorCode.FORBIDDEN.getCode(),
            ErrorCode.FORBIDDEN.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * 处理其他运行时异常（未被明确捕获的业务异常）
     * @param ex 运行时异常对象
     * @param request HTTP 请求对象
     * @return 400 Bad Request 响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Runtime exception: ", ex);
        ErrorResponse errorResponse = ErrorResponse.of(
            "RUNTIME_ERROR",
            ex.getMessage() != null ? ex.getMessage() : "An error occurred",
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 处理未预期的系统异常（兜底处理）
     * @param ex 异常对象
     * @param request HTTP 请求对象
     * @return 500 Internal Server Error 响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: ", ex);
        ErrorResponse errorResponse = ErrorResponse.of(
            ErrorCode.UNKNOWN_ERROR.getCode(),
            ErrorCode.UNKNOWN_ERROR.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
