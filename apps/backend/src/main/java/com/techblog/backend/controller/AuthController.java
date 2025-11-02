package com.techblog.backend.controller;

import com.techblog.backend.config.XssProperties;
import com.techblog.backend.dto.*;
import com.techblog.backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final XssProperties xssProperties;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, 
                                      HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        
        if (xssProperties.isSecure()) {
            // SECURE mode: Set HttpOnly Cookie
            setSecureCookie(response, authResponse.getAccessToken());
            return ResponseEntity.noContent().build();
        } else {
            // VULN mode: Return JWT in response body
            return ResponseEntity.ok(authResponse);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, 
                                   HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        
        if (xssProperties.isSecure()) {
            // SECURE mode: Set HttpOnly Cookie
            setSecureCookie(response, authResponse.getAccessToken());
            return ResponseEntity.noContent().build();
        } else {
            // VULN mode: Return JWT in response body
            return ResponseEntity.ok(authResponse);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        if (xssProperties.isSecure()) {
            // Clear cookie
            Cookie cookie = new Cookie("access", "");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        UserDto user = authService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }
    
    private void setSecureCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("access", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60); // 30 minutes
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }
}
