package com.socialnetwork.service;

import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import java.util.List;

public interface MessageService {
    List<MessageResponseTo> getAll();
    MessageResponseTo getById(Long id);
    MessageResponseTo create(MessageRequestTo request);
    MessageResponseTo update(Long id, MessageRequestTo request);
    void delete(Long id);
    List<MessageResponseTo> getByTweetId(Long tweetId);
}