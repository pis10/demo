package com.techblog.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDto {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String contentHtml;
    private Integer likesCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private UserDto author;
    private List<TagDto> tags;
}
