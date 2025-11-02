package com.techblog.backend.controller;

import com.techblog.backend.config.XssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {
    
    private final XssProperties xssProperties;
    
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String q) {
        String message;
        
        if (xssProperties.isVuln()) {
            // VULN mode: Direct concatenation without escaping (XSS L0/L1 entry point)
            message = "为您找到『" + q + "』的结果…";
        } else {
            // SECURE mode: HTML escape the user input
            message = "为您找到『" + HtmlUtils.htmlEscape(q) + "』的结果…";
        }
        
        // In a real app, you would search articles here
        // For demo purposes, return empty results
        return Map.of(
            "message", message,
            "items", List.of()
        );
    }
}
