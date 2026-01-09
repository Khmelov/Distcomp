package org.example.task330.discussion.service;

import java.util.List;
import org.example.task330.discussion.dto.MessageRequestTo;
import org.example.task330.discussion.dto.MessageResponseTo;

public interface MessageService {
    MessageResponseTo create(MessageRequestTo request);

    MessageResponseTo getById(String country, Long tweetId, Long id);

    List<MessageResponseTo> getAllByTweetId(String country, Long tweetId);

    List<MessageResponseTo> getAll();

    MessageResponseTo update(String country, Long tweetId, Long id, MessageRequestTo request);

    void delete(String country, Long tweetId, Long id);
}

