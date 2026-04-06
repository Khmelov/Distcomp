package com.messageservice.services;

import com.messageservice.configs.exceptionhandlerconfig.exceptions.MessageNotFoundException;
import com.messageservice.configs.exceptionhandlerconfig.exceptions.TweetNotFoundException;
import com.messageservice.configs.tweetclientconfig.TweetClient;
import com.messageservice.dtos.MessageRequestTo;
import com.messageservice.dtos.MessageResponseTo;
import com.messageservice.models.Message;
import com.messageservice.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final TweetClient tweetClient;

    public MessageResponseTo createMessage(MessageRequestTo request) {
        validateTweetExists(request.getTweetId());
        Message saved = messageRepository.save(toEntity(request));
        return toDto(saved);
    }

    public List<MessageResponseTo> findAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public MessageResponseTo findMessageById(Long id) {
        Message message = messageRepository.findMessageById(id)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
        return toDto(message);
    }

    public MessageResponseTo updateMessageById(MessageRequestTo request, Long id) {
        Message message = messageRepository.findMessageById(id)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        message.setContent(request.getContent());
        Message updated = messageRepository.save(message);
        return toDto(updated);
    }

    public void deleteMessageById(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new MessageNotFoundException("Message not found");
        }
        messageRepository.deleteById(id);
    }

    public void deleteMessageByTweetId(Long tweetId) {
        messageRepository.deleteAllByTweetId(tweetId);
    }

    public List<MessageResponseTo> findMessagesByTweetId(Long tweetId) {
        return messageRepository.findAllByTweetId(tweetId);
    }

    private void validateTweetExists(Long tweetId) {
        try {
            tweetClient.getTweetById(tweetId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new TweetNotFoundException("Tweet not found");
        }
    }

    private Message toEntity(MessageRequestTo request) {
        return Message.builder()
                .content(request.getContent())
                .tweetId(request.getTweetId())
                .build();
    }

    private MessageResponseTo toDto(Message entity) {
        return MessageResponseTo.builder()
                .id(entity.getId())
                .tweetId(entity.getTweetId())
                .content(entity.getContent())
                .build();
    }
}
