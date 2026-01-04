package com.blog.discussion.mapper;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.blog.discussion.model.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        message.setEditorId(request.getEditorId());

        // Сохраняем состояние из запроса, если есть
        if (request.getState() != null) {
            message.setState(request.getState());
        } else {
            message.setState("PENDING");
        }

        message.setCreated(LocalDateTime.now());
        message.setModified(LocalDateTime.now());

        return message;
    }

    public MessageResponseTo toResponse(Message message) {
        if (message == null) {
            return null;
        }

        MessageResponseTo response = new MessageResponseTo();
        response.setId(message.getId());
        response.setTopicId(message.getTopicId());
        response.setContent(message.getContent());
        response.setEditorId(message.getEditorId());
        response.setCountry(message.getCountry());
        response.setState(message.getState());
        response.setCreated(message.getCreated());
        response.setModified(message.getModified());

        return response;
    }
}

