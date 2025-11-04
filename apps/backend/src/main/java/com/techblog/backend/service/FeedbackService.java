package com.techblog.backend.service;

import com.techblog.backend.common.exception.ResourceNotFoundException;
import com.techblog.backend.config.XssProperties;
import com.techblog.backend.dto.FeedbackDto;
import com.techblog.backend.dto.FeedbackRequest;
import com.techblog.backend.entity.Feedback;
import com.techblog.backend.mapper.FeedbackMapper;
import com.techblog.backend.repository.FeedbackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * 反馈服务
 * 负责用户反馈的提交和管理逻辑
 * 
 * XSS 场景 5：盲 XSS 攻击场景
 * - VULN 模式：直接存储用户提交的内容，管理员查看时触发 XSS
 * - SECURE 模式：对内容进行 HTML 转义后存储，防止 XSS 攻击
 */
@Service
public class FeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final XssProperties xssProperties;
    
    /**
     * 构造函数，注入依赖
     */
    public FeedbackService(
        FeedbackRepository feedbackRepository,
        FeedbackMapper feedbackMapper,
        XssProperties xssProperties
    ) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
        this.xssProperties = xssProperties;
    }
    
    /**
     * 提交用户反馈（无需登录）
     * 
     * XSS 场景 5 入口点：
     * - VULN 模式：直接存储用户提交的内容（盲 XSS 漏洞）
     * - SECURE 模式：对内容进行 HTML 转义后存储，防止 XSS
     */
    @Transactional
    public void submitFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setEmail(request.getEmail());
        
        if (xssProperties.isSecure()) {
            // SECURE 模式：HTML 转义后存储
            feedback.setContentHtml(HtmlUtils.htmlEscape(request.getContent()));
        } else {
            // VULN 模式：直接存储（盲 XSS 漏洞）
            feedback.setContentHtml(request.getContent());
        }
        
        feedback.setStatus(Feedback.FeedbackStatus.NEW);
        feedbackRepository.save(feedback);
    }
    
    public Page<FeedbackDto> getAllFeedbacks(Pageable pageable) {
        return feedbackRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(feedbackMapper::toDto);
    }
    
    public FeedbackDto getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feedback", id));
        return feedbackMapper.toDto(feedback);
    }
    
    @Transactional
    public void markAsRead(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feedback", id));
        feedback.setStatus(Feedback.FeedbackStatus.READ);
        feedbackRepository.save(feedback);
    }
}
