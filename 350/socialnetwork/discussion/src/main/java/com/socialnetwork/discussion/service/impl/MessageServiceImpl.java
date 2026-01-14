package com.socialnetwork.discussion.service.impl;

import com.socialnetwork.discussion.dto.request.MessageRequestDto;
import com.socialnetwork.discussion.dto.response.MessageResponseDto;
import com.socialnetwork.discussion.model.Message;
import com.socialnetwork.discussion.repository.MessageRepository;
import com.socialnetwork.discussion.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<MessageResponseDto> getAll() {
        return messageRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponseDto getById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        return toResponse(message);
    }

    @Override
    public MessageResponseDto create(MessageRequestDto request) {
        Message message = new Message();
        message.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        message.setTweetId(request.getTweetId());
        message.setId(Math.abs(UUID.randomUUID().getMostSignificantBits())); // Положительный ID
        message.setContent(request.getContent());

        Message savedMessage = messageRepository.save(message);
        return toResponse(savedMessage);
    }

    @Override
    public MessageResponseDto update(Long id, MessageRequestDto request) {
        Message existingMessage = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));

        existingMessage.setContent(request.getContent());
        existingMessage.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        existingMessage.setTweetId(request.getTweetId());

        Message updatedMessage = messageRepository.save(existingMessage);
        return toResponse(updatedMessage);
    }

    @Override
    public void delete(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));

        messageRepository.delete(message);
    }

    @Override
    public List<MessageResponseDto> getByTweetId(Long tweetId) {
        return messageRepository.findByTweetId(tweetId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseDto> getByCountryAndTweetId(String country, Long tweetId) {
        return messageRepository.findByCountryAndTweetId(country, tweetId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MessageResponseDto toResponse(Message message) {
        return new MessageResponseDto(
                message.getCountry(),
                message.getTweetId(),
                message.getId(),
                message.getContent()
        );
    }
}