package com.techblog.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * 
 * 使用 Caffeine 作为本地缓存实现，提升系统性能。
 * 
 * 缓存策略：
 * - config: 配置信息缓存，不过期（手动失效）
 * - users: 用户信息缓存，10 分钟过期
 * - articles: 文章列表缓存，5 分钟过期
 * - tags: 标签列表缓存，30 分钟过期
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 配置 Caffeine 缓存管理器
     * 
     * @return CacheManager 实例
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "config",    // 配置缓存
            "users",     // 用户缓存
            "articles",  // 文章缓存
            "tags"       // 标签缓存
        );
        
        // 配置缓存策略
        cacheManager.setCaffeine(Caffeine.newBuilder()
            // 最大缓存条目数
            .maximumSize(1000)
            // 写入后过期时间（10 分钟）
            .expireAfterWrite(10, TimeUnit.MINUTES)
            // 启用统计信息收集
            .recordStats()
        );
        
        return cacheManager;
    }
}
