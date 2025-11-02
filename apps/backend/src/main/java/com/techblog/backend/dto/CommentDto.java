package com.techblog.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String contentHtml;
    private LocalDateTime createdAt;
    private UserDto user;
}
