package com.techblog.backend.service;

import com.techblog.backend.common.exception.ResourceNotFoundException;
import com.techblog.backend.dto.FeedbackDto;
import com.techblog.backend.dto.FeedbackRequest;
import com.techblog.backend.entity.Feedback;
import com.techblog.backend.mapper.FeedbackMapper;
import com.techblog.backend.repository.FeedbackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 反馈服务
 * 负责用户反馈的提交和管理逻辑
 * 
 * 安全注意：
 * - 此服务涉及 XSS 场景 5 盲 XSS 攻击场景
 */
@Service
public class FeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    
    /**
     * 构造函数，注入依赖
     * @param feedbackRepository 反馈仓库
     * @param feedbackMapper 反馈对象映射器
     */
    public FeedbackService(FeedbackRepository feedbackRepository, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
    }
    
    @Transactional
    public void submitFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setEmail(request.getEmail());
        feedback.setContentHtml(request.getContent());
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
