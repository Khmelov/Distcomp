package org.example.task310rest.repository;

import org.example.task310rest.model.Message;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRepository extends InMemoryCrudRepository<Message> {
    public MessageRepository() {
        super(Message::getId, Message::setId);
    }
}


