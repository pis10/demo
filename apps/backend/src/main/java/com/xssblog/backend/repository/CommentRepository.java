package com.xssblog.backend.repository;

import com.xssblog.backend.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论仓库接口
 * 使用 EntityGraph 优化关联查询，解决 N+1 查询问题
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 查询文章的所有评论（按创建时间降序，预加载用户信息）
     * 使用 EntityGraph 一次性加载评论的用户信息，避免 N+1 查询
     */
    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);
}
