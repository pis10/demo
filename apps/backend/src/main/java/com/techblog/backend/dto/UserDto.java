package com.techblog.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private String bannerUrl;
    private String bio;
    private LocalDateTime createdAt;
}
