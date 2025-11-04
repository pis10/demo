package com.xssblog.backend.mapper;

import com.xssblog.backend.dto.UserDto;
import com.xssblog.backend.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用户对象映射器
 * 
 * 职责：
 * - 负责 User 实体与 UserDto 之间的相互转换
 * - 处理关联对象的转换逻辑
 * - 避免在 Service 层直接进行对象映射
 */
@Component
public class UserMapper {
    
    /**
     * 将用户实体转换为 DTO
     * 
     * @param user 用户实体对象
     * @return UserDto 数据传输对象
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBannerUrl(user.getBannerUrl());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
    
    /**
     * 将用户实体转换为简化 DTO（用于嵌套展示）
     * 只包含基本信息，不包含敏感字段
     * 
     * @param user 用户实体对象
     * @return UserDto 简化的数据传输对象
     */
    public UserDto toSimpleDto(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBannerUrl(user.getBannerUrl());
        dto.setBio(user.getBio());
        return dto;
    }
}
