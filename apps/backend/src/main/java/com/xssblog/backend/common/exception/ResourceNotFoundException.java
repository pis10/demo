package com.xssblog.backend.common.exception;

import com.xssblog.backend.common.enums.ErrorCode;

/**
 * 资源未找到异常
 * 
 * 当请求的资源不存在时抛出此异常，例如：
 * - 文章不存在
 * - 用户不存在
 * - 评论不存在
 */
public class ResourceNotFoundException extends BusinessException {
    
    /**
     * 使用默认错误码构造异常
     */
    public ResourceNotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }
    
    /**
     * 使用自定义消息构造异常
     * 
     * @param message 自定义错误消息
     */
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
    
    /**
     * 使用资源类型和 ID 构造异常
     * 
     * @param resourceType 资源类型（如 "Article", "User"）
     * @param resourceId 资源 ID
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(ErrorCode.RESOURCE_NOT_FOUND, 
              String.format("%s not found with id: %s", resourceType, resourceId));
    }
}
