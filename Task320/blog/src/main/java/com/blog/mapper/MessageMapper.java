package com.blog.mapper;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.model.Message;
import com.blog.model.Topic;
import com.blog.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    @Autowired
    private TopicRepository topicRepository;

    public Message toEntity(MessageRequestTo request) {
        if (request == null) {
            return null;
        }

        Message message = new Message();

        // Находим топик
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with id: " + request.getTopicId()));
        message.setTopic(topic);

        message.setContent(request.getContent());
        return message;
    }

    public MessageResponseTo toResponse(Message entity) {
        if (entity == null) {
            return null;
        }

        MessageResponseTo response = new MessageResponseTo();
        response.setId(entity.getId());
        response.setTopicId(entity.getTopic().getId());
        response.setContent(entity.getContent());
        return response;
    }
}