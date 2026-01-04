package com.task310.socialnetwork.service;

import com.task310.socialnetwork.dto.request.MessageRequestTo;
import com.task310.socialnetwork.dto.response.MessageResponseTo;
import java.util.List;

public interface MessageService {
    List<MessageResponseTo> getAll();
    MessageResponseTo getById(Long id);
    MessageResponseTo create(MessageRequestTo request);
    MessageResponseTo update(Long id, MessageRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    List<MessageResponseTo> getByTweetId(Long tweetId);
}