package com.messageservice.repositories;

import com.messageservice.dtos.MessageResponseTo;
import com.messageservice.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findMessageById(Long id);

    List<MessageResponseTo> findAllByTweetId(Long tweetId);

    boolean existsMessageByTweetId(Long tweetId);

    void deleteAllByTweetId(Long tweetId);
}
