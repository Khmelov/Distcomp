package com.socialnetwork.service.impl;

import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import com.socialnetwork.exception.ResourceNotFoundException;
import com.socialnetwork.mapper.MessageMapper;
import com.socialnetwork.model.Message;
import com.socialnetwork.model.Tweet;
import com.socialnetwork.repository.MessageRepository;
import com.socialnetwork.repository.TweetRepository;
import com.socialnetwork.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private TweetRepository tweetRepository;

    @Override
    public List<MessageResponseTo> getAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MessageResponseTo> getAll(Pageable pageable) {
        return messageRepository.findAll(pageable)
                .map(messageMapper::toResponse);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return messageMapper.toResponse(message);
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        Tweet tweet = tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + request.getTweetId()));

        Message message = messageMapper.toEntity(request);
        message.setTweet(tweet);

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toResponse(savedMessage);
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (!message.getTweet().getId().equals(request.getTweetId())) {
            Tweet tweet = tweetRepository.findById(request.getTweetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + request.getTweetId()));
            message.setTweet(tweet);
        }

        message.setContent(request.getContent());

        Message updatedMessage = messageRepository.save(message);
        return messageMapper.toResponse(updatedMessage);
    }

    @Override
    public void delete(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message not found with id: " + id);
        }
        messageRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return messageRepository.existsById(id);
    }

    @Override
    public List<MessageResponseTo> getByTweetId(Long tweetId) {
        List<Message> messages = messageRepository.findByTweetId(tweetId);
        return messages.stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MessageResponseTo> getByTweetId(Long tweetId, Pageable pageable) {
        Page<Message> messages = messageRepository.findByTweetId(tweetId, pageable);
        return messages.map(messageMapper::toResponse);
    }
}