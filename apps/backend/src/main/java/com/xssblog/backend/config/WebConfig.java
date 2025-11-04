package com.xssblog.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 配置跨域资源共享（CORS）策略
 * 
 * 允许的源：
 * - localhost:5173 (Vite 开发服务器)
 * - 127.0.0.1:5173 (本地回环地址)
 * - localhost:80/localhost (生产环境)
 * 
 * 注意：仅用于开发和教学环境，生产环境需要更严格的 CORS 策略
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 配置 CORS 映射规则
     * 允许前端从不同源访问后端 API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 允许的源地址（包含 localhost 和 127.0.0.1）
                .allowedOrigins(
                    "http://localhost:5173",
                    "http://127.0.0.1:5173",
                    "http://localhost:80",
                    "http://localhost"
                )
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头（白名单）
                .allowedHeaders(
                    "Content-Type",
                    "Authorization",
                    "X-Requested-With",
                    "Accept",
                    "Origin"
                )
                // 允许携带凭证（Cookie、Authorization 头等）
                .allowCredentials(true)
                // 预检请求缓存时间（秒）
                .maxAge(3600);
    }
}
