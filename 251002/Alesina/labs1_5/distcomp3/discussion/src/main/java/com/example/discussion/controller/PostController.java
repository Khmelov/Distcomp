package com.example.discussion.controller;

import com.example.discussion.dto.PostRequestTo;
import com.example.discussion.dto.PostResponseTo;
import com.example.discussion.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
public class PostController {


    private final PostService postService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String REQUEST_TOPIC = "InTopic";
    private static final String REPLY_TOPIC = "OutTopic";

    public PostController(PostService postService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.postService = postService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = REQUEST_TOPIC,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}" // Используем group-id из конфига
    )
    public void handleKafkaRequest(
            @Payload PostRequestTo request,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("operation") String operation,
            @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic,
            Acknowledgment acknowledgment) { // Добавляем параметр для ручного подтверждения

        try {

            Object response;
            switch (operation) {
                case "getAll":
                    response = postService.findAll();
                    break;
                case "getById":
                    response = postService.findById(request.getId());
                    break;
                case "create":
                    response = postService.save(request);
                    break;
                case "update":
                    response = postService.update(request);
                    break;
                case "delete":
                    response = postService.deleteById(request.getId());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + operation);
            }


            if (replyTopic != null) {
                sendReply(replyTopic, correlationId, response);
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            if (replyTopic != null) {
                sendErrorReply(replyTopic, correlationId, e);
            }
        }
    }

    private void sendReply(String replyTopic, String correlationId, Object response) {
        Message<Object> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, replyTopic)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .setHeader(KafkaHeaders.KEY, correlationId)
                .build();

        kafkaTemplate.send(message);
    }

    private void sendErrorReply(String replyTopic, String correlationId, Exception e) {
        Message<String> message = MessageBuilder
                .withPayload("Error: " + e.getMessage())
                .setHeader(KafkaHeaders.TOPIC, replyTopic)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .setHeader(KafkaHeaders.KEY, correlationId)
                .setHeader("error", true)
                .build();

        kafkaTemplate.send(message);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseTo>> getAllPosts() {
        List<PostResponseTo> posts = postService.findAll();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        PostResponseTo post = postService.findById(id);
        return post != null
                ? new ResponseEntity<>(post, HttpStatus.OK)
                : new ResponseEntity<>(new Error("Post not found"), HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<PostResponseTo> createPost(@RequestBody @Valid PostRequestTo postRequestTo) {
        PostResponseTo createdPost = postService.save(postRequestTo);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<PostResponseTo> updatePost(@RequestBody @Valid PostRequestTo postRequestTo) {
        PostResponseTo updatedPost = postService.update(postRequestTo);
        return updatedPost != null
                ? new ResponseEntity<>(updatedPost, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}