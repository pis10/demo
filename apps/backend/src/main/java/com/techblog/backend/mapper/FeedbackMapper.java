package com.techblog.backend.mapper;

import com.techblog.backend.dto.FeedbackDto;
import com.techblog.backend.entity.Feedback;
import org.springframework.stereotype.Component;

/**
 * 反馈对象映射器
 * 
 * 职责：
 * - 负责 Feedback 实体与 FeedbackDto 之间的转换
 */
@Component
public class FeedbackMapper {
    
    /**
     * 将反馈实体转换为 DTO
     * 
     * @param feedback 反馈实体对象
     * @return FeedbackDto 数据传输对象
     */
    public FeedbackDto toDto(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        
        FeedbackDto dto = new FeedbackDto();
        dto.setId(feedback.getId());
        dto.setEmail(feedback.getEmail());
        dto.setContentHtml(feedback.getContentHtml());
        dto.setStatus(feedback.getStatus().name());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
