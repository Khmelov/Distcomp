package com.labs.service.service;

import com.labs.domain.entity.Message;
import com.labs.domain.entity.Tweet;
import com.labs.domain.repository.MessageRepository;
import com.labs.domain.repository.TweetRepository;
import com.labs.service.dto.MessageDto;
import com.labs.service.exception.ResourceNotFoundException;
import com.labs.service.exception.ValidationException;
import com.labs.service.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final TweetRepository tweetRepository;
    private final MessageMapper messageMapper;

    public MessageDto create(MessageDto messageDto) {
        validateMessageDto(messageDto);
        Tweet tweet = tweetRepository.findById(messageDto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + messageDto.getTweetId() + " not found"));
        
        Message message = messageMapper.toEntity(messageDto);
        message.setTweet(tweet);
        
        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> findAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MessageDto findById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message with id " + id + " not found"));
        return messageMapper.toDto(message);
    }

    public MessageDto update(Long id, MessageDto messageDto) {
        validateMessageDto(messageDto);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message with id " + id + " not found"));
        
        Tweet tweet = tweetRepository.findById(messageDto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + messageDto.getTweetId() + " not found"));
        
        message.setTweet(tweet);
        message.setContent(messageDto.getContent());
        
        Message updated = messageRepository.save(message);
        return messageMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message with id " + id + " not found");
        }
        messageRepository.deleteById(id);
    }

    private void validateMessageDto(MessageDto messageDto) {
        if (messageDto == null) {
            throw new ValidationException("Message data cannot be null");
        }
        if (messageDto.getTweetId() == null) {
            throw new ValidationException("Tweet ID cannot be null");
        }
        if (messageDto.getContent() == null || messageDto.getContent().trim().isEmpty()) {
            throw new ValidationException("Content cannot be null or empty");
        }
        if (messageDto.getContent().length() < 2 || messageDto.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 2 and 2048 characters");
        }
    }
}

