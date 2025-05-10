package com.example.distcomp_1.repository;

import com.example.distcomp_1.mdoel.Message;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class MessageRepository {
    private Long currentId = 0L;
    private final HashMap<Long, Message> messages = new HashMap<>();

    public Message add(Message message) {
        message.setId(currentId++);
        messages.put(message.getId(), message);
        return message;
    }
    public Message get(long id) {
        return messages.get(id);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages.values());
    }
    public void delete(long id) {
        messages.remove(id);
    }

    public Message update(Message message) {
        messages.put(message.getId(), message);
        return message;
    }
}
