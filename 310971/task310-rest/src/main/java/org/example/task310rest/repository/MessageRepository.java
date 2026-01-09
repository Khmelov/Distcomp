package org.example.task310rest.repository;

import org.example.task310rest.model.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MessageRepository extends InMemoryCrudRepository<Message> {
    public MessageRepository() {
        super(Message::getId, Message::setId);
    }

    public List<Message> findByTweetId(Long tweetId) {
        return findAll().stream()
                .filter(message -> tweetId.equals(message.getTweetId()))
                .collect(Collectors.toList());
    }
}
