package org.example.task340.publisher.controller;

import org.example.task340.publisher.kafka.KafkaMessageConsumer;
import org.example.task340.publisher.kafka.KafkaMessageProducer;
import org.example.task340.publisher.kafka.KafkaMessageRequest;
import org.example.task340.publisher.kafka.KafkaMessageResponse;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final KafkaMessageProducer kafkaProducer;
    private final KafkaMessageConsumer kafkaConsumer;

    public MessageController(KafkaMessageProducer kafkaProducer,
                            KafkaMessageConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> request) {
        try {
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("POST");
            kafkaRequest.setTweetId(((Number) request.get("tweetId")).longValue());
            kafkaRequest.setContent((String) request.get("content"));
            kafkaRequest.setCountry(request.containsKey("country") && request.get("country") != null ?
                    (String) request.get("country") : "default");

            String requestId = kafkaProducer.sendMessage(kafkaRequest);

            // For POST, return immediately with PENDING state (as per diagram)
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", System.currentTimeMillis()); // Temporary ID, will be replaced by discussion
            response.put("tweetId", kafkaRequest.getTweetId());
            response.put("content", kafkaRequest.getContent());
            response.put("country", kafkaRequest.getCountry());
            response.put("state", "PENDING");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            log.error("Error creating message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(@RequestParam(required = false) String country,
                                                             @RequestParam(required = false) Long tweetId) {
        try {
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("GET");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 1);

            if (response == null || response.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            List<KafkaMessageResponse> kafkaMessages = response.getMessages();
            List<Map<String, Object>> messages = kafkaMessages.stream()
                .map(msg -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", msg.getId());
                    map.put("tweetId", msg.getTweetId());
                    map.put("content", msg.getContent());
                    map.put("country", msg.getCountry());
                    map.put("state", msg.getState() != null ? msg.getState() : "PENDING");
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(messages);
        } catch (Exception ex) {
            log.error("Error getting messages via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("country") String country,
                                                        @PathVariable("tweetId") Long tweetId,
                                                        @PathVariable("id") Long id) {
        try {
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("GET");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);
            kafkaRequest.setId(id);

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 1);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            if (response.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            Map<String, Object> message = new java.util.HashMap<>();
            message.put("id", response.getId());
            message.put("tweetId", response.getTweetId());
            message.put("content", response.getContent());
            message.put("country", response.getCountry());
            message.put("state", response.getState() != null ? response.getState() : "PENDING");

            return ResponseEntity.ok(message);
        } catch (Exception ex) {
            log.error("Error getting message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getByIdLegacy(@PathVariable("id") Long id) {
        try {
            // First get all messages to find the one with this ID
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("GET");

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 1);

            if (response == null || response.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            List<KafkaMessageResponse> kafkaMessages = response.getMessages();
            KafkaMessageResponse found = kafkaMessages.stream()
                .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                .findFirst()
                .orElse(null);

            if (found == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Map<String, Object> message = new java.util.HashMap<>();
            message.put("id", found.getId());
            message.put("tweetId", found.getTweetId());
            message.put("content", found.getContent());
            message.put("country", found.getCountry());
            message.put("state", found.getState() != null ? found.getState() : "PENDING");
            return ResponseEntity.ok(message);
        } catch (Exception ex) {
            log.error("Error getting message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable("country") String country,
                                                       @PathVariable("tweetId") Long tweetId,
                                                       @PathVariable("id") Long id,
                                                       @RequestBody Map<String, Object> request) {
        try {
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("PUT");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);
            kafkaRequest.setId(id);
            kafkaRequest.setContent((String) request.get("content"));

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 1);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            if (response.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            Map<String, Object> message = new java.util.HashMap<>();
            message.put("id", response.getId());
            message.put("tweetId", response.getTweetId());
            message.put("content", response.getContent());
            message.put("country", response.getCountry());
            message.put("state", response.getState() != null ? response.getState() : "PENDING");

            return ResponseEntity.ok(message);
        } catch (Exception ex) {
            log.error("Error updating message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLegacy(@PathVariable("id") Long id,
                                                             @RequestBody Map<String, Object> request) {
        try {
            // First get all messages to find the one with this ID
            KafkaMessageRequest getRequest = new KafkaMessageRequest();
            getRequest.setOperation("GET");

            String getRequestId = kafkaProducer.sendMessage(getRequest);
            KafkaMessageResponse getResponse = kafkaConsumer.waitForResponse(getRequestId, 1);

            if (getResponse == null || getResponse.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            List<KafkaMessageResponse> kafkaMessages = getResponse.getMessages();
            KafkaMessageResponse found = kafkaMessages.stream()
                .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                .findFirst()
                .orElse(null);

            if (found == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String country = found.getCountry();
            Long tweetId = found.getTweetId();

            // Now update
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("PUT");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);
            kafkaRequest.setId(id);
            kafkaRequest.setContent((String) request.get("content"));

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 1);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            if (response.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            Map<String, Object> message = new java.util.HashMap<>();
            message.put("id", response.getId());
            message.put("tweetId", response.getTweetId());
            message.put("content", response.getContent());
            message.put("country", response.getCountry());
            message.put("state", response.getState() != null ? response.getState() : "PENDING");

            return ResponseEntity.ok(message);
        } catch (Exception ex) {
            log.error("Error updating message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Void> delete(@PathVariable("country") String country,
                                       @PathVariable("tweetId") Long tweetId,
                                       @PathVariable("id") Long id) {
        try {
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("DELETE");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);
            kafkaRequest.setId(id);

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 1);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            if (response.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            log.error("Error deleting message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegacy(@PathVariable("id") Long id) {
        try {
            // First get all messages to find the one with this ID
            KafkaMessageRequest getRequest = new KafkaMessageRequest();
            getRequest.setOperation("GET");

            String getRequestId = kafkaProducer.sendMessage(getRequest);
            KafkaMessageResponse getResponse = kafkaConsumer.waitForResponse(getRequestId, 1);

            if (getResponse == null || getResponse.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            List<KafkaMessageResponse> kafkaMessages = getResponse.getMessages();
            KafkaMessageResponse found = kafkaMessages.stream()
                .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                .findFirst()
                .orElse(null);

            if (found == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String country = found.getCountry();
            Long tweetId = found.getTweetId();

            // Now delete
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("DELETE");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);
            kafkaRequest.setId(id);

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 1);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            if (response.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            log.error("Error deleting message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
