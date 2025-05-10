package com.example.rest.service.MessageService.Implementation;

import com.example.rest.client.DiscussionClient;
import com.example.rest.dto.requestDto.MessageRequestTo;
import com.example.rest.dto.responseDto.MessageResponseTo;
import com.example.rest.dto.updateDto.MessageUpdateTo;
import com.example.rest.entity.Story;
import com.example.rest.exeption.StoryNotFoundException;
import com.example.rest.repository.StoryRepo;
import com.example.rest.service.MessageService.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageServiceRest implements MessageService {
    private final DiscussionClient discussionClient;
    private final StoryRepo storyRepo;

    @Cacheable(value = "messagesList")
    public List<MessageResponseTo> getAll() {
        return discussionClient.getAllMessages();
    }

    @Cacheable(value = "messages", key = "#id")
    public MessageResponseTo get(long id) {
        return discussionClient.getMessageById(id);
    }
    @Caching(
            put = @CachePut(value = "messages", key = "#result.id"),
            evict = @CacheEvict(value = "messagesList", allEntries = true)
    )
    public MessageResponseTo create(MessageRequestTo input) {
        Story story = storyRepo.findById(input.getStoryId()).orElseThrow(() -> new StoryNotFoundException("Story with id " + input.getStoryId() + " not found"));
        MessageResponseTo message = discussionClient.createMessage(input);
        story.getMessages().add(message.getId());
        storyRepo.update(story);
        return message;
    }
    @Caching(
            put = @CachePut(value = "messages", key = "#input.id"),
            evict = @CacheEvict(value = "messagesList", allEntries = true)
    )
    public MessageResponseTo update(MessageUpdateTo input) {
        Story story = storyRepo.findById(input.getStoryId()).orElseThrow(()-> new StoryNotFoundException("Story with id " + input.getStoryId() + " not found"));
        MessageResponseTo message = discussionClient.updateMessage(input);
        story.getMessages().add(message.getId());
        storyRepo.update(story);
        return message;
    }
    @Caching(
            evict = {
                    @CacheEvict(value = "messages", key = "#id"),
                    @CacheEvict(value = "messagesList", allEntries = true)
            }
    )
    public void delete(long id) {
        Story story = storyRepo.findByMessagesContaining(id);
        if (story != null) {
            story.getMessages().remove(id);
            storyRepo.update(story);
        }try {
            discussionClient.deleteMessage(id);
        } catch (HttpClientErrorException.NotFound ex) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
