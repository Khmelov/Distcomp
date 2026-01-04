package com.blog.mapper;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.model.Topic;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class TopicMapper {

    public Topic toEntity(TopicRequestTo request) {
        if (request == null) {
            return null;
        }

        Topic topic = new Topic();
        topic.setEditorId(request.getEditorId());
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setTagIds(request.getTagIds());
        // created и modified устанавливаются репозиторием
        return topic;
    }

    public TopicResponseTo toResponse(Topic entity) {
        if (entity == null) {
            return null;
        }

        TopicResponseTo response = new TopicResponseTo();
        response.setId(entity.getId());
        response.setEditorId(entity.getEditorId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());
        response.setTagIds(entity.getTagIds());
        return response;
    }
}