package com.techblog.backend.repository;

import com.techblog.backend.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 文章仓库接口
 * 使用 EntityGraph 优化关联查询，解决 N+1 查询问题
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * 根据 slug 查询文章（预加载作者和标签）
     * 使用 EntityGraph 一次性加载关联对象，避免额外查询
     */
    @EntityGraph(attributePaths = {"author", "tags"})
    Optional<Article> findBySlug(String slug);
    
    /**
     * 查询所有文章（按发布时间降序，预加载作者和标签）
     */
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Article> findAllByOrderByPublishedAtDesc(Pageable pageable);
    
    /**
     * 查询指定作者的文章（按发布时间降序，预加载作者和标签）
     */
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Article> findByAuthorUsernameOrderByPublishedAtDesc(String username, Pageable pageable);
}
