package com.socialnetwork.controller.v2;

import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import com.socialnetwork.exception.ResourceNotFoundException;
import com.socialnetwork.exception.UnauthorizedException;
import com.socialnetwork.model.Tweet;
import com.socialnetwork.repository.TweetRepository;
import com.socialnetwork.security.SecurityUtil;
import com.socialnetwork.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/messages")
public class MessageControllerV2 {

    @Autowired
    private MessageService messageService;

    @Autowired
    private TweetRepository tweetRepository;

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        List<MessageResponseTo> messages = messageService.getAll();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable Long id) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        MessageResponseTo message = messageService.getById(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@Valid @RequestBody MessageRequestTo request) {
        // ADMIN - полный доступ, CUSTOMER - может создавать только для своих твитов
        if (SecurityUtil.isCustomer()) {
            Tweet tweet = tweetRepository.findById(request.getTweetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + request.getTweetId()));
            Long tweetOwnerId = tweet.getUser().getId();
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            if (!tweetOwnerId.equals(currentUserId)) {
                throw new UnauthorizedException("You can only create messages for your own tweets");
            }
        }
        
        // Если country не указан, устанавливаем значение по умолчанию
        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            request.setCountry("US");
        }

        MessageResponseTo createdMessage = messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(@PathVariable Long id,
                                                           @Valid @RequestBody MessageRequestTo request) {
        // ADMIN - полный доступ, CUSTOMER - только свои сообщения (через свои твиты)
        if (SecurityUtil.isCustomer()) {
            // Получаем сообщение, чтобы узнать tweetId
            MessageResponseTo existingMessage = messageService.getById(id);
            Tweet tweet = tweetRepository.findById(existingMessage.getTweetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + existingMessage.getTweetId()));
            Long tweetOwnerId = tweet.getUser().getId();
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            if (!tweetOwnerId.equals(currentUserId)) {
                throw new UnauthorizedException("You can only update messages for your own tweets");
            }
            // Проверяем, что пользователь не пытается изменить tweetId
            if (!request.getTweetId().equals(existingMessage.getTweetId())) {
                throw new UnauthorizedException("You cannot change the tweet of your message");
            }
        }
        
        // Если country не указан, устанавливаем значение по умолчанию
        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            request.setCountry("US");
        }

        MessageResponseTo updatedMessage = messageService.update(id, request);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        // ADMIN - полный доступ, CUSTOMER - только свои сообщения (через свои твиты)
        if (SecurityUtil.isCustomer()) {
            MessageResponseTo message = messageService.getById(id);
            Tweet tweet = tweetRepository.findById(message.getTweetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + message.getTweetId()));
            Long tweetOwnerId = tweet.getUser().getId();
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            if (!tweetOwnerId.equals(currentUserId)) {
                throw new UnauthorizedException("You can only delete messages for your own tweets");
            }
        }
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<MessageResponseTo>> getMessagesByTweetId(@PathVariable Long tweetId) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        List<MessageResponseTo> messages = messageService.getByTweetId(tweetId);
        return ResponseEntity.ok(messages);
    }
}

