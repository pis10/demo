package com.techblog.backend.common.exception;

import com.techblog.backend.common.enums.ErrorCode;

/**
 * 用户已存在异常
 * 
 * 当注册时用户名或邮箱已被占用时抛出此异常。
 */
public class UserAlreadyExistsException extends BusinessException {
    
    /**
     * 使用默认错误码和消息构造异常
     */
    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }
    
    /**
     * 使用自定义消息构造异常
     * 
     * @param message 自定义错误消息
     */
    public UserAlreadyExistsException(String message) {
        super(ErrorCode.USER_ALREADY_EXISTS, message);
    }
    
    /**
     * 使用字段名构造异常
     * 
     * @param field 重复的字段名（如 "username", "email"）
     */
    public UserAlreadyExistsException(String field, String value) {
        super(ErrorCode.USER_ALREADY_EXISTS, 
              String.format("%s '%s' is already in use", field, value));
    }
}
