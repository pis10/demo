package com.techblog.backend.controller;

import com.techblog.backend.dto.FeedbackRequest;
import com.techblog.backend.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    @PostMapping("/feedback")
    public ResponseEntity<Map<String, Object>> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        feedbackService.submitFeedback(request);
        return ResponseEntity.ok(Map.of("ok", true, "message", "感谢您的反馈！"));
    }
}
