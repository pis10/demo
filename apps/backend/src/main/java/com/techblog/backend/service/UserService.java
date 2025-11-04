package com.techblog.backend.service;

import com.techblog.backend.common.exception.ResourceNotFoundException;
import com.techblog.backend.dto.UserDto;
import com.techblog.backend.entity.User;
import com.techblog.backend.mapper.UserMapper;
import com.techblog.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 * 
 * 主要功能：
 * - 根据用户名查询用户信息
 * - 更新用户 Bio（个人简介）——XSS 场景 4 攻击入口
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    /**
     * 构造函数，注入依赖
     * @param userRepository 用户仓库
     * @param userMapper 用户对象映射器
     */
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    
    /**
     * 根据用户名查询用户信息
     * 
     * @param username 用户名
     * @return 用户 DTO 对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return userMapper.toDto(user);
    }
    
    /**
     * 更新用户 Bio（XSS 场景 4 入口点）
     * 
     * 安全注意：
     * - VULN 模式：直接存储用户提交的 HTML，可能包含 XSS 攻击代码
     * - SECURE 模式：前端使用 DOMPurify 过滤后再显示
     * 
     * @param username 用户名
     * @param bio 新的 Bio 内容（支持 HTML）
     * @return 更新后的用户 DTO
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    @Transactional
    public UserDto updateBio(String username, String bio) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        user.setBio(bio);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
