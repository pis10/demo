package com.techblog.backend.service;

import com.techblog.backend.common.exception.ResourceNotFoundException;
import com.techblog.backend.dto.*;
import com.techblog.backend.entity.Article;
import com.techblog.backend.mapper.ArticleMapper;
import com.techblog.backend.mapper.CommentMapper;
import com.techblog.backend.repository.ArticleRepository;
import com.techblog.backend.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务
 * 负责文章和评论的查询业务逻辑
 */
@Service
@Transactional(readOnly = true)
public class ArticleService {
    
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    
    /**
     * 构造函数，注入依赖
     * @param articleRepository 文章仓库
     * @param commentRepository 评论仓库
     * @param articleMapper 文章对象映射器
     * @param commentMapper 评论对象映射器
     */
    public ArticleService(ArticleRepository articleRepository,
                         CommentRepository commentRepository,
                         ArticleMapper articleMapper,
                         CommentMapper commentMapper) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
    }
    
    public Page<ArticleDto> getAllArticles(Pageable pageable) {
        return articleRepository.findAllByOrderByPublishedAtDesc(pageable)
            .map(articleMapper::toDto);
    }
    
    public ArticleDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        return articleMapper.toDto(article);
    }
    
    public ArticleDto getArticleBySlug(String slug) {
        Article article = articleRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Article with slug: " + slug));
        return articleMapper.toDto(article);
    }
    
    public List<CommentDto> getArticleComments(Long articleId) {
        return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId)
            .stream()
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
    }
    
    public Page<ArticleDto> getArticlesByAuthor(String username, Pageable pageable) {
        return articleRepository.findByAuthorUsernameOrderByPublishedAtDesc(username, pageable)
            .map(articleMapper::toDto);
    }
}
