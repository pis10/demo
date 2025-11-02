package com.techblog.backend.repository;

import com.techblog.backend.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);
    Page<Article> findAllByOrderByPublishedAtDesc(Pageable pageable);
    Page<Article> findByAuthorUsernameOrderByPublishedAtDesc(String username, Pageable pageable);
}
