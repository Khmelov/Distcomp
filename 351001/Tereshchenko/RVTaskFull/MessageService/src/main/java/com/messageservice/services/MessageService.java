package com.messageservice.services;

import com.messageservice.configs.cassandraconfig.DiscussionCassandraProperties;
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
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final int ID_SHIFT = 20;
    private static final long SEQUENCE_MASK = (1L << ID_SHIFT) - 1;

    private final MessageRepository messageRepository;
    private final TweetClient tweetClient;
    private final DiscussionCassandraProperties cassandraProperties;
    private final AtomicLong sequence = new AtomicLong();

    public MessageResponseTo createMessage(MessageRequestTo request) {
        validateTweetExists(request.getTweetId());
        Message saved = messageRepository.save(toEntity(request, nextId()));
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
        Message message = messageRepository.findMessageById(id)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        messageRepository.delete(message);
    }

    public void deleteMessageByTweetId(Long tweetId) {
        messageRepository.deleteAllByTweetId(tweetId);
    }

    public List<MessageResponseTo> findMessagesByTweetId(Long tweetId) {
        return messageRepository.findAllByTweetId(tweetId).stream()
                .map(this::toDto)
                .toList();
    }

    private void validateTweetExists(Long tweetId) {
        try {
            tweetClient.getTweetById(tweetId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new TweetNotFoundException("Tweet not found");
        }
    }

    private Message toEntity(MessageRequestTo request, Long id) {
        int bucket = resolveBucket(id);
        return Message.builder()
                .id(id)
                .content(request.getContent())
                .tweetId(request.getTweetId())
                .bucket(bucket)
                .build();
    }

    private MessageResponseTo toDto(Message entity) {
        return MessageResponseTo.builder()
                .id(entity.getId())
                .tweetId(entity.getTweetId())
                .content(entity.getContent())
                .build();
    }

    private Long nextId() {
        long timestampPart = System.currentTimeMillis() << ID_SHIFT;
        long sequencePart = sequence.getAndIncrement() & SEQUENCE_MASK;
        return timestampPart | sequencePart;
    }

    private int resolveBucket(Long id) {
        return Math.floorMod(Long.hashCode(id), cassandraProperties.getBucketCount());
    }
}
