package by.bsuir.rest.client;

import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;

import java.util.List;

public interface DiscussionClient {

    MessageResponseTo create(MessageRequestTo request);

    MessageResponseTo getById(Long id);

    List<MessageResponseTo> getAll();

    List<MessageResponseTo> findByStoryId(Long storyId);

    MessageResponseTo update(Long id, MessageRequestTo request);

    void delete(Long id);
}
