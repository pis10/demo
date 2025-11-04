package com.techblog.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求数据传输对象
 * 用于用户登录
 */
public class LoginRequest {
    /**
     * 用户名（必填）
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码（必填）
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    // Getter 和 Setter 方法
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
