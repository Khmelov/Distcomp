package com.example.rest.service;

import com.example.rest.client.DiscussionClient;
import com.example.rest.dto.requestDto.MessageRequestTo;
import com.example.rest.dto.responseDto.MessageResponseTo;
import com.example.rest.dto.updateDto.MessageUpdateTo;
import com.example.rest.entity.Message;
import com.example.rest.entity.Story;
import com.example.rest.exeption.ExternalServiceException;
import com.example.rest.exeption.StoryNotFoundException;
import com.example.rest.mapper.MessageMapper;
import com.example.rest.repository.StoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {
    private final DiscussionClient discussionClient;
    private final StoryRepo storyRepo;

    public List<MessageResponseTo> getAll() {
        return discussionClient.getAllMessages();
    }

    public MessageResponseTo get(long id) {
        return discussionClient.getMessageById(id);
    }
    public MessageResponseTo create(MessageRequestTo input) {
        Story story = storyRepo.findById(input.getStoryId()).orElseThrow(() -> new StoryNotFoundException("Story with id " + input.getStoryId() + " not found"));
        MessageResponseTo message = discussionClient.createMessage(input);
        story.getMessages().add(message.getId());
        storyRepo.update(story);
        return message;
    }
    public MessageResponseTo update(MessageUpdateTo input) {
        Story story = storyRepo.findById(input.getStoryId()).orElseThrow(()-> new StoryNotFoundException("Story with id " + input.getStoryId() + " not found"));
        MessageResponseTo message = discussionClient.updateMessage(input);
        story.getMessages().add(message.getId());
        storyRepo.update(story);
        return message;
    }

    public boolean delete(long id) {
        Story story = storyRepo.findByMessagesContaining(id);
        story.getMessages().remove(id);
        storyRepo.update(story);
        return discussionClient.deleteMessage(id);
    }

}
