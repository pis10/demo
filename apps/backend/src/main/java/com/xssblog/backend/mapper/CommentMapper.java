package com.xssblog.backend.mapper;

import com.xssblog.backend.dto.CommentDto;
import com.xssblog.backend.entity.Comment;
import org.springframework.stereotype.Component;

/**
 * 评论对象映射器
 * 
 * 职责：
 * - 负责 Comment 实体与 CommentDto 之间的转换
 * - 处理评论的用户信息关联
 */
@Component
public class CommentMapper {
    
    private final UserMapper userMapper;
    
    /**
     * 构造函数注入依赖的映射器
     * 
     * @param userMapper 用户映射器
     */
    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    /**
     * 将评论实体转换为 DTO
     * 
     * @param comment 评论实体对象
     * @return CommentDto 数据传输对象
     */
    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContentHtml(comment.getContentHtml());
        dto.setCreatedAt(comment.getCreatedAt());
        
        // 转换关联的用户信息
        dto.setUser(userMapper.toSimpleDto(comment.getUser()));
        
        return dto;
    }
}
