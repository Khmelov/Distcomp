package by.bsuir.rest.service;

import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;

import java.util.List;

public interface MessageService {

    MessageResponseTo create(MessageRequestTo request);

    MessageResponseTo getById(Long id);

    List<MessageResponseTo> getAll();

    List<MessageResponseTo> findByStoryId(Long storyId);

    MessageResponseTo update(Long pathId, MessageRequestTo request);

    void delete(Long id);
}
