package com.techblog.backend.common.enums;

/**
 * 错误码枚举
 * 
 * 定义系统中所有业务错误码及其对应的 HTTP 状态码和默认消息。
 * 错误码命名规则：大写字母 + 下划线分隔
 */
public enum ErrorCode {
    
    // ==================== 通用错误 (1xxx) ====================
    
    /**
     * 未知错误
     */
    UNKNOWN_ERROR("UNKNOWN_ERROR", "An unknown error occurred", 500),
    
    /**
     * 参数校验失败
     */
    VALIDATION_FAILED("VALIDATION_FAILED", "Validation failed", 400),
    
    /**
     * 资源未找到
     */
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Resource not found", 404),
    
    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "Method not allowed", 405),
    
    // ==================== 认证/授权错误 (2xxx) ====================
    
    /**
     * 未认证（未登录）
     */
    UNAUTHORIZED("UNAUTHORIZED", "Authentication required", 401),
    
    /**
     * 无权限访问
     */
    FORBIDDEN("FORBIDDEN", "Access denied", 403),
    
    /**
     * 无效的凭证（用户名或密码错误）
     */
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid username or password", 401),
    
    /**
     * Token 无效或已过期
     */
    INVALID_TOKEN("INVALID_TOKEN", "Invalid or expired token", 401),
    
    // ==================== 用户相关错误 (3xxx) ====================
    
    /**
     * 用户已存在（用户名或邮箱重复）
     */
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "User already exists", 409),
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", 404),
    
    /**
     * 邮箱已被使用
     */
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email is already in use", 409),
    
    // ==================== 文章相关错误 (4xxx) ====================
    
    /**
     * 文章不存在
     */
    ARTICLE_NOT_FOUND("ARTICLE_NOT_FOUND", "Article not found", 404),
    
    /**
     * 文章标题重复
     */
    ARTICLE_TITLE_EXISTS("ARTICLE_TITLE_EXISTS", "Article title already exists", 409),
    
    // ==================== 评论相关错误 (5xxx) ====================
    
    /**
     * 评论不存在
     */
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "Comment not found", 404),
    
    /**
     * 评论内容为空
     */
    COMMENT_EMPTY("COMMENT_EMPTY", "Comment content cannot be empty", 400),
    
    // ==================== 反馈相关错误 (6xxx) ====================
    
    /**
     * 反馈不存在
     */
    FEEDBACK_NOT_FOUND("FEEDBACK_NOT_FOUND", "Feedback not found", 404);
    
    // ==================== 枚举字段 ====================
    
    /**
     * 错误码（用于 API 响应）
     */
    private final String code;
    
    /**
     * 错误消息（默认英文消息）
     */
    private final String message;
    
    /**
     * HTTP 状态码
     */
    private final int status;
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param status HTTP 状态码
     */
    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getStatus() {
        return status;
    }
}
