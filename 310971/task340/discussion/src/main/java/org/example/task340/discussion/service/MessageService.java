package org.example.task340.discussion.service;

import java.util.List;
import org.example.task340.discussion.dto.MessageRequestTo;
import org.example.task340.discussion.dto.MessageResponseTo;
import org.example.task340.discussion.model.Message;

public interface MessageService {
    MessageResponseTo create(MessageRequestTo request);

    MessageResponseTo getById(String country, Long tweetId, Long id);

    List<MessageResponseTo> getAllByTweetId(String country, Long tweetId);

    List<MessageResponseTo> getAll();

    MessageResponseTo update(String country, Long tweetId, Long id, MessageRequestTo request);

    void delete(String country, Long tweetId, Long id);
    
    // Additional methods for Kafka integration
    Message getEntityById(String country, Long tweetId, Long id);
    
    void save(Message entity);
}

