package com.blog.discussion.mapper;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.blog.discussion.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public Message toEntity(MessageRequestTo request, String country, Long id) {
        if (request == null) {
            return null;
        }

        Message message = new Message();
        message.setCountry(country);
        message.setTopicId(request.getTopicId());
        message.setId(id);
        message.setContent(request.getContent());

        return message;
    }

    public MessageResponseTo toResponse(Message entity) {
        if (entity == null) {
            return null;
        }

        MessageResponseTo response = new MessageResponseTo();
        response.setCountry(entity.getCountry());
        response.setTopicId(entity.getTopicId());
        response.setId(entity.getId());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());

        return response;
    }
}