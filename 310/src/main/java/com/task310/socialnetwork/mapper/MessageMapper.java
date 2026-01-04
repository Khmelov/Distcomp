package com.task310.socialnetwork.mapper;

import com.task310.socialnetwork.dto.request.MessageRequestTo;
import com.task310.socialnetwork.dto.response.MessageResponseTo;
import com.task310.socialnetwork.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public Message toEntity(MessageRequestTo request) {
        if (request == null) {
            return null;
        }

        Message message = new Message();
        message.setTweetId(request.getTweetId());
        message.setContent(request.getContent());
        return message;
    }

    public MessageResponseTo toResponse(Message entity) {
        if (entity == null) {
            return null;
        }

        MessageResponseTo response = new MessageResponseTo();
        response.setId(entity.getId());
        response.setTweetId(entity.getTweetId());
        response.setContent(entity.getContent());
        return response;
    }
}