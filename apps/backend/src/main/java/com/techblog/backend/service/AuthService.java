package com.techblog.backend.service;

import com.techblog.backend.common.exception.InvalidCredentialsException;
import com.techblog.backend.common.exception.ResourceNotFoundException;
import com.techblog.backend.common.exception.UserAlreadyExistsException;
import com.techblog.backend.dto.*;
import com.techblog.backend.entity.User;
import com.techblog.backend.mapper.UserMapper;
import com.techblog.backend.repository.UserRepository;
import com.techblog.backend.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务
 * 负责用户注册、登录和令牌生成
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    
    /**
     * 构造函数，注入依赖
     * @param userRepository 用户仓库
     * @param passwordEncoder 密码加密器
     * @param jwtTokenProvider JWT 令牌提供者
     * @param userMapper 用户对象映射器
     */
    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider jwtTokenProvider,
                      UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.USER);
        user.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getUsername());
        
        userRepository.save(user);
        
        // 生成 JWT Token
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token);
    }
    
    public AuthResponse login(LoginRequest request) {
        // 查找用户
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new InvalidCredentialsException());
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        
        // 生成 JWT Token
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token);
    }
    
    public UserDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        return userMapper.toDto(user);
    }
}
