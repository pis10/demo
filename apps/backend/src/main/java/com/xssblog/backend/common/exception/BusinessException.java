package com.xssblog.backend.common.exception;

import com.xssblog.backend.common.enums.ErrorCode;

/**
 * 业务异常基类
 * 
 * 用于封装业务层的异常信息，包括错误码、错误消息和 HTTP 状态码。
 * 所有具体的业务异常应该继承此类。
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final String code;
    
    /**
     * HTTP 状态码
     */
    private final int status;
    
    /**
     * 使用错误码枚举构造异常
     * 
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }
    
    /**
     * 使用错误码枚举和自定义消息构造异常
     * 
     * @param errorCode 错误码枚举
     * @param message 自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }
    
    /**
     * 使用完整参数构造异常
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param status HTTP 状态码
     */
    public BusinessException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
    
    public String getCode() {
        return code;
    }
    
    public int getStatus() {
        return status;
    }
}
