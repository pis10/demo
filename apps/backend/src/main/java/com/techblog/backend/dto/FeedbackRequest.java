package com.techblog.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 反馈请求数据传输对象
 * 用于用户提交反馈
 */
public class FeedbackRequest {
    /**
     * 邮箱（必填，合法的邮箱格式，最长 128 个字符）
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过 128 个字符")
    private String email;
    
    /**
     * 反馈内容（必填，最长 5000 个字符）
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 5000, message = "反馈内容不能超过 5000 个字符")
    private String content;

    // Getter 和 Setter 方法
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
