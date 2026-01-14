package com.socialnetwork.discussion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.discussion.dto.kafka.KafkaMessageRequest;
import com.socialnetwork.discussion.dto.kafka.KafkaMessageResponse;
import com.socialnetwork.discussion.model.Message;
import com.socialnetwork.discussion.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    private static final String IN_TOPIC = "InTopic";
    private static final String OUT_TOPIC = "OutTopic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = IN_TOPIC)
    public void listen(String messageJson) {
        try {
            System.out.println("=== Received Kafka message ===");
            System.out.println("Raw message: " + messageJson);

            // Десериализуем JSON в объект
            KafkaMessageRequest request = objectMapper.readValue(messageJson, KafkaMessageRequest.class);

            System.out.println("Operation: " + request.getOperation());
            System.out.println("RequestId: " + request.getRequestId());
            System.out.println("TweetId: " + request.getTweetId());
            System.out.println("==============================");

            KafkaMessageResponse response = processRequest(request);
            sendResponse(request.getRequestId(), response);
        } catch (Exception e) {
            System.err.println("Error processing Kafka request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private KafkaMessageResponse processRequest(KafkaMessageRequest request) {
        System.out.println("Processing operation: " + request.getOperation());

        switch (request.getOperation()) {
            case "CREATE":
                return createMessage(request);
            case "GET":
                return getMessage(request);
            case "GET_ALL":
                return getAllMessages(request);
            case "GET_BY_TWEET":
                return getMessagesByTweet(request);
            case "UPDATE":
                return updateMessage(request);
            case "DELETE":
                return deleteMessage(request);
            default:
                throw new IllegalArgumentException("Unknown operation: " + request.getOperation());
        }
    }

    private KafkaMessageResponse createMessage(KafkaMessageRequest request) {
        System.out.println("Creating message for tweet: " + request.getTweetId());

        // Модерация контента
        String state = moderateContent(request.getContent());

        Message message = new Message(
                request.getCountry() != null ? request.getCountry() : "US",
                request.getTweetId(),
                generateId(),
                request.getContent(),
                state
        );

        Message saved = messageRepository.save(message);

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setSuccess(true);
        response.setMessageId(saved.getId());
        response.setCountry(saved.getCountry());
        response.setTweetId(saved.getTweetId());
        response.setContent(saved.getContent());
        response.setState(saved.getState());

        System.out.println("Message created with id: " + saved.getId() + ", state: " + saved.getState());
        return response;
    }

    private KafkaMessageResponse getMessage(KafkaMessageRequest request) {
        System.out.println("Getting message with id: " + request.getMessageId());

        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + request.getMessageId()));

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setSuccess(true);
        response.setMessageId(message.getId());
        response.setCountry(message.getCountry());
        response.setTweetId(message.getTweetId());
        response.setContent(message.getContent());
        response.setState(message.getState());

        return response;
    }

    private KafkaMessageResponse getAllMessages(KafkaMessageRequest request) {
        System.out.println("Getting all messages");

        List<Message> messages = messageRepository.findAll();

        List<KafkaMessageResponse> messageResponses = messages.stream()
                .map(this::convertToKafkaResponse)
                .collect(Collectors.toList());

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setSuccess(true);
        response.setMessages(messageResponses);

        return response;
    }

    private KafkaMessageResponse getMessagesByTweet(KafkaMessageRequest request) {
        System.out.println("Getting messages for tweet: " + request.getTweetId());

        List<Message> messages = messageRepository.findByTweetId(request.getTweetId());

        List<KafkaMessageResponse> messageResponses = messages.stream()
                .map(this::convertToKafkaResponse)
                .collect(Collectors.toList());

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setSuccess(true);
        response.setMessages(messageResponses);

        return response;
    }

    private KafkaMessageResponse updateMessage(KafkaMessageRequest request) {
        System.out.println("Updating message with id: " + request.getMessageId());

        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + request.getMessageId()));

        // Модерация обновленного контента
        String state = moderateContent(request.getContent());

        message.setContent(request.getContent());
        message.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        message.setTweetId(request.getTweetId());
        message.setState(state);

        Message updated = messageRepository.save(message);

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setSuccess(true);
        response.setMessageId(updated.getId());
        response.setCountry(updated.getCountry());
        response.setTweetId(updated.getTweetId());
        response.setContent(updated.getContent());
        response.setState(updated.getState());

        return response;
    }

    private KafkaMessageResponse deleteMessage(KafkaMessageRequest request) {
        System.out.println("Deleting message with id: " + request.getMessageId());

        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + request.getMessageId()));

        messageRepository.delete(message);

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setSuccess(true);

        return response;
    }

    private String moderateContent(String content) {
        if (content == null) {
            return "APPROVE";
        }

        // Простая модерация на основе стоп-слов
        String[] stopWords = {"spam", "scam", "fraud", "illegal"};

        for (String word : stopWords) {
            if (content.toLowerCase().contains(word)) {
                System.out.println("Message declined due to stop word: " + word);
                return "DECLINE";
            }
        }

        return "APPROVE";
    }

    private Long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

    private KafkaMessageResponse convertToKafkaResponse(Message message) {
        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setMessageId(message.getId());
        response.setCountry(message.getCountry());
        response.setTweetId(message.getTweetId());
        response.setContent(message.getContent());
        response.setState(message.getState());
        return response;
    }

    private void sendResponse(UUID requestId, KafkaMessageResponse response) {
        response.setRequestId(requestId);
        String key = "response-" + requestId;
        System.out.println("Sending response to OutTopic, key: " + key);
        try {
            kafkaTemplate.send(OUT_TOPIC, key, response);
            System.out.println("Response sent successfully");
        } catch (Exception e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }
}