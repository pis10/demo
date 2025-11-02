package com.techblog.backend.controller;

import com.techblog.backend.dto.FeedbackDto;
import com.techblog.backend.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final FeedbackService feedbackService;
    
    @GetMapping("/feedbacks")
    public ResponseEntity<Page<FeedbackDto>> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feedbackService.getAllFeedbacks(pageable));
    }
    
    @GetMapping("/feedbacks/{id}")
    public ResponseEntity<FeedbackDto> getFeedbackById(@PathVariable Long id) {
        FeedbackDto feedback = feedbackService.getFeedbackById(id);
        feedbackService.markAsRead(id);
        return ResponseEntity.ok(feedback);
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        // Simple dashboard data for demo
        return ResponseEntity.ok(Map.of(
            "todayVisits", 1247,
            "newUsers", 23,
            "totalArticles", 3,
            "pendingFeedbacks", 2
        ));
    }
}
