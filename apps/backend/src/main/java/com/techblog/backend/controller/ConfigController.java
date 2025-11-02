package com.techblog.backend.controller;

import com.techblog.backend.config.XssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConfigController {
    
    private final XssProperties xssProperties;
    
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        return Map.of("xssMode", xssProperties.getMode());
    }
    
    @PostMapping("/config/mode")
    public ResponseEntity<Map<String, Object>> switchMode(@RequestBody Map<String, String> request) {
        String newMode = request.get("mode");
        if ("vuln".equalsIgnoreCase(newMode) || "secure".equalsIgnoreCase(newMode)) {
            xssProperties.setMode(newMode);
            return ResponseEntity.ok(Map.of(
                "xssMode", xssProperties.getMode(),
                "message", "Mode switched to " + xssProperties.getMode()
            ));
        }
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Invalid mode. Use 'vuln' or 'secure'"
        ));
    }
}
