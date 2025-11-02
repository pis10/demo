package com.techblog.backend.controller;

import com.techblog.backend.dto.ArticleDto;
import com.techblog.backend.dto.UserDto;
import com.techblog.backend.service.ArticleService;
import com.techblog.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final UserService userService;
    private final ArticleService articleService;
    
    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserProfile(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        UserDto user = userService.getUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleDto> articles = articleService.getArticlesByAuthor(username, pageable);
        
        return ResponseEntity.ok(Map.of(
            "user", user,
            "articles", articles
        ));
    }
    
    @PostMapping("/bio")
    public ResponseEntity<Map<String, Object>> updateBio(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        String bio = request.get("bio");
        UserDto updated = userService.updateBio(username, bio);
        
        return ResponseEntity.ok(Map.of("ok", true, "user", updated));
    }
}
