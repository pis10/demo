package com.xssblog.backend.common.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一错误响应类
 * 
 * 用于封装 API 错误响应的格式，确保前后端错误信息传递的一致性。
 */
public class ErrorResponse {
    
    /**
     * 错误码（如 USER_ALREADY_EXISTS）
     */
    private String code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 时间戳（ISO 8601 格式）
     */
    private String timestamp;
    
    /**
     * 请求路径
     */
    private String path;
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param path 请求路径
     */
    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.path = path;
    }
    
    /**
     * 静态工厂方法：创建错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param path 请求路径
     * @return ErrorResponse 实例
     */
    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, path);
    }
    
    // Getter 和 Setter 方法
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
}
