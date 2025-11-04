package com.techblog.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cookie 安全配置类
 * 
 * 用于配置 JWT Cookie 的安全属性，支持不同环境下的灵活配置。
 * 
 * 配置来源：application.yml 中的 security.cookie
 */
@Configuration
@ConfigurationProperties(prefix = "security.cookie")
public class CookieProperties {
    
    /**
     * 是否启用 HttpOnly 属性（防止 JavaScript 访问 Cookie）
     * 默认值：true
     */
    private Boolean httpOnly = true;
    
    /**
     * 是否启用 Secure 属性（仅通过 HTTPS 传输）
     * 默认值：false（开发环境）
     * 生产环境建议设置为 true
     */
    private Boolean secure = false;
    
    /**
     * SameSite 属性值（防止 CSRF 攻击）
     * 可选值：Strict, Lax, None
     * 默认值：Strict
     */
    private String sameSite = "Strict";
    
    /**
     * Cookie 最大存活时间（秒）
     * 默认值：1800 秒（30 分钟）
     */
    private Integer maxAge = 1800;
    
    // Getter 和 Setter 方法
    
    public Boolean getHttpOnly() {
        return httpOnly;
    }
    
    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
    
    public Boolean getSecure() {
        return secure;
    }
    
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }
    
    public String getSameSite() {
        return sameSite;
    }
    
    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }
    
    public Integer getMaxAge() {
        return maxAge;
    }
    
    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }
}
