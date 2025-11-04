package com.techblog.backend.mapper;

import com.techblog.backend.dto.ArticleDto;
import com.techblog.backend.entity.Article;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 文章对象映射器
 * 
 * 职责：
 * - 负责 Article 实体与 ArticleDto 之间的转换
 * - 处理文章的作者、标签等关联对象
 * - 支持完整转换和简化转换
 */
@Component
public class ArticleMapper {
    
    private final UserMapper userMapper;
    private final TagMapper tagMapper;
    
    /**
     * 构造函数注入依赖的映射器
     * 
     * @param userMapper 用户映射器
     * @param tagMapper 标签映射器
     */
    public ArticleMapper(UserMapper userMapper, TagMapper tagMapper) {
        this.userMapper = userMapper;
        this.tagMapper = tagMapper;
    }
    
    /**
     * 将文章实体转换为 DTO（包含完整信息）
     * 
     * @param article 文章实体对象
     * @return ArticleDto 数据传输对象
     */
    public ArticleDto toDto(Article article) {
        if (article == null) {
            return null;
        }
        
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setExcerpt(article.getExcerpt());
        dto.setContentHtml(article.getContentHtml());
        dto.setLikesCount(article.getLikesCount());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCreatedAt(article.getCreatedAt());
        
        // 转换关联对象
        dto.setAuthor(userMapper.toSimpleDto(article.getAuthor()));
        dto.setTags(article.getTags().stream()
            .map(tagMapper::toDto)
            .collect(Collectors.toList()));
        
        return dto;
    }
    

}
