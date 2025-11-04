package com.techblog.backend.mapper;

import com.techblog.backend.dto.TagDto;
import com.techblog.backend.entity.Tag;
import org.springframework.stereotype.Component;

/**
 * 标签对象映射器
 * 
 * 职责：
 * - 负责 Tag 实体与 TagDto 之间的转换
 */
@Component
public class TagMapper {
    
    /**
     * 将标签实体转换为 DTO
     * 
     * @param tag 标签实体对象
     * @return TagDto 数据传输对象
     */
    public TagDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setColor(tag.getColor());
        return dto;
    }
}
