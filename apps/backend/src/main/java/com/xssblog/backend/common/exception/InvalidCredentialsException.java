package com.xssblog.backend.common.exception;

import com.xssblog.backend.common.enums.ErrorCode;

/**
 * 无效凭证异常
 * 
 * 当用户登录时提供的用户名或密码错误时抛出此异常。
 */
public class InvalidCredentialsException extends BusinessException {
    
    /**
     * 使用默认错误码和消息构造异常
     */
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }
    
    /**
     * 使用自定义消息构造异常
     * 
     * @param message 自定义错误消息
     */
    public InvalidCredentialsException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS, message);
    }
}
