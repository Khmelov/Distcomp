package org.example.task310rest.service;

import java.util.List;
import org.example.task310rest.dto.MessageRequestTo;
import org.example.task310rest.dto.MessageResponseTo;

public interface MessageService {
    MessageResponseTo create(MessageRequestTo request);

    MessageResponseTo getById(Long id);

    List<MessageResponseTo> getAll();

    MessageResponseTo update(Long id, MessageRequestTo request);

    void delete(Long id);
}


