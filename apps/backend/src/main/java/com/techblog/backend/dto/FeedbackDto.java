package com.techblog.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackDto {
    private Long id;
    private String email;
    private String contentHtml;
    private String status;
    private LocalDateTime createdAt;
}
