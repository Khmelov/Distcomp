package com.socialnetwork.service;

import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MessageService {
    List<MessageResponseTo> getAll();
    Page<MessageResponseTo> getAll(Pageable pageable);
    MessageResponseTo getById(Long id);
    MessageResponseTo create(MessageRequestTo request);
    MessageResponseTo update(Long id, MessageRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    List<MessageResponseTo> getByTweetId(Long tweetId);
    Page<MessageResponseTo> getByTweetId(Long tweetId, Pageable pageable);
}