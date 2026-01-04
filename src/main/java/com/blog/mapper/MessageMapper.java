package com.blog.mapper;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public Message toEntity(MessageRequestTo request) {
        if (request == null) {
            return null;
        }

        Message message = new Message();
        message.setTopicId(request.getTopicId());
        message.setContent(request.getContent());
        return message;
    }

    public MessageResponseTo toResponse(Message entity) {
        if (entity == null) {
            return null;
        }

        MessageResponseTo response = new MessageResponseTo();
        response.setId(entity.getId());
        response.setTopicId(entity.getTopicId());
        response.setContent(entity.getContent());
        return response;
    }
}