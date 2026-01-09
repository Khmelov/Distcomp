package org.example.task350.publisher.controller;

import org.example.task350.publisher.kafka.KafkaMessageConsumer;
import org.example.task350.publisher.kafka.KafkaMessageProducer;
import org.example.task350.publisher.kafka.KafkaMessageRequest;
import org.example.task350.publisher.kafka.KafkaMessageResponse;
import org.example.task350.publisher.service.CacheService;

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
    private static final String CACHE_KEY_PREFIX = "message:";
    private static final String CACHE_KEY_ALL = "message:all";
    
    private final KafkaMessageProducer kafkaProducer;
    private final KafkaMessageConsumer kafkaConsumer;
    private final CacheService cacheService;

    public MessageController(KafkaMessageProducer kafkaProducer,
                            KafkaMessageConsumer kafkaConsumer,
                            CacheService cacheService) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
        this.cacheService = cacheService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> request) {
        try {
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("POST");
            
            // Safely extract tweetId
            Object tweetIdObj = request.get("tweetId");
            if (tweetIdObj == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            kafkaRequest.setTweetId(((Number) tweetIdObj).longValue());
            
            kafkaRequest.setContent((String) request.get("content"));
            kafkaRequest.setCountry(request.containsKey("country") && request.get("country") != null ?
                    (String) request.get("country") : "default");

            String requestId = kafkaProducer.sendMessage(kafkaRequest);

            // Wait for response from Kafka to get the real ID
            KafkaMessageResponse kafkaResponse = kafkaConsumer.waitForResponse(requestId, 3);
            
            Map<String, Object> response = new java.util.HashMap<>();
            if (kafkaResponse != null && kafkaResponse.getError() == null) {
                // Use real ID from Kafka response
                response.put("id", kafkaResponse.getId());
                response.put("tweetId", kafkaResponse.getTweetId());
                response.put("content", kafkaResponse.getContent());
                response.put("country", kafkaResponse.getCountry());
                response.put("state", kafkaResponse.getState() != null ? kafkaResponse.getState() : "PENDING");
                
                // Cache the response
                String cacheKey = CACHE_KEY_PREFIX + kafkaResponse.getCountry() + ":" + kafkaResponse.getTweetId() + ":" + kafkaResponse.getId();
                cacheService.put(cacheKey, response);
                // Invalidate all messages cache
                cacheService.delete(CACHE_KEY_ALL);
            } else {
                // Fallback: return with temporary ID if Kafka response failed
                response.put("id", System.currentTimeMillis());
                response.put("tweetId", kafkaRequest.getTweetId());
                response.put("content", kafkaRequest.getContent());
                response.put("country", kafkaRequest.getCountry());
                response.put("state", "PENDING");
                
                // Invalidate cache for messages
                cacheService.deletePattern(CACHE_KEY_PREFIX + "*");
                cacheService.delete(CACHE_KEY_ALL);
            }

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
            // Build cache key
            String cacheKey = CACHE_KEY_ALL;
            if (country != null && tweetId != null) {
                cacheKey = CACHE_KEY_PREFIX + country + ":" + tweetId;
            }
            
            // Try to get from cache first
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cached = (List<Map<String, Object>>) cacheService.get(cacheKey, List.class);
            if (cached != null) {
                log.debug("Cache hit for messages: {}", cacheKey);
                return ResponseEntity.ok(cached);
            }
            
            log.debug("Cache miss for messages: {}, fetching via Kafka", cacheKey);
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
            
            // Cache the result
            cacheService.put(cacheKey, messages);
            
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
            String cacheKey = CACHE_KEY_PREFIX + country + ":" + tweetId + ":" + id;
            
            // Always get fresh data from Kafka to avoid stale cache
            // (messages can be updated directly in discussion module)
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("GET");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);
            kafkaRequest.setId(id);

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 3);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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

            // Update cache with fresh data
            cacheService.put(cacheKey, message);
            // Also update CACHE_KEY_ALL
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> allMessages = (List<Map<String, Object>>) cacheService.get(CACHE_KEY_ALL, List.class);
            if (allMessages == null) {
                allMessages = new java.util.ArrayList<>();
            }
            // Remove old entry if exists
            allMessages.removeIf(msg -> {
                Object msgId = msg.get("id");
                return msgId != null && (msgId instanceof Number ? ((Number) msgId).longValue() == id : msgId.toString().equals(String.valueOf(id)));
            });
            // Add updated entry
            allMessages.add(message);
            cacheService.put(CACHE_KEY_ALL, allMessages);
            
            return ResponseEntity.ok(message);
        } catch (Exception ex) {
            log.error("Error getting message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getByIdLegacy(@PathVariable("id") Long id) {
        try {
            // Try to get from cache first (read-through cache)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cached = (List<Map<String, Object>>) cacheService.get(CACHE_KEY_ALL, List.class);
            
            if (cached != null) {
                Map<String, Object> found = cached.stream()
                    .filter(msg -> {
                        Object msgId = msg.get("id");
                        return msgId != null && (msgId instanceof Number ? ((Number) msgId).longValue() == id : msgId.toString().equals(String.valueOf(id)));
                    })
                    .findFirst()
                    .orElse(null);
                
                if (found != null) {
                    log.debug("Cache hit for message: id={}", id);
                    // Return from cache, but also update cache in background if needed
                    // For now, return cached data for fast response
                    return ResponseEntity.ok(found);
                }
            }
            
            // Cache miss - get from Kafka and update cache (read-through cache pattern)
            KafkaMessageResponse response = null;
            KafkaMessageResponse found = null;
            int maxRetries = 2;
            int timeoutSeconds = 2; // Increased timeout to 2 seconds
            
            for (int retry = 0; retry < maxRetries; retry++) {
                KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
                kafkaRequest.setOperation("GET");
                
                String requestId = kafkaProducer.sendMessage(kafkaRequest);
                
                // Add delay before waiting for response to allow Kafka to process updates
                if (retry > 0) {
                    try {
                        Thread.sleep(500 * retry); // Increasing delay with each retry (500ms, 1s)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    try {
                        Thread.sleep(300); // Small initial delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                response = kafkaConsumer.waitForResponse(requestId, timeoutSeconds);
                
                if (response != null && response.getError() == null) {
                    List<KafkaMessageResponse> kafkaMessages = response.getMessages();
                    if (kafkaMessages != null && !kafkaMessages.isEmpty()) {
                        found = kafkaMessages.stream()
                            .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                            .findFirst()
                            .orElse(null);
                        
                        if (found != null) {
                            break; // Found the message, exit retry loop
                        }
                    }
                }
                
                if (retry < maxRetries - 1) {
                    try {
                        Thread.sleep(200); // Delay between retries
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (response == null) {
                log.warn("Timeout waiting for GET response after {} retries: id={}, assuming not found", maxRetries, id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (response.getError() != null) {
                log.error("Error in GET response: {}", response.getError());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            if (found == null) {
                log.warn("Message not found after {} retries: id={}", maxRetries, id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Map<String, Object> message = new java.util.HashMap<>();
            message.put("id", found.getId());
            message.put("tweetId", found.getTweetId());
            message.put("content", found.getContent());
            message.put("country", found.getCountry());
            message.put("state", found.getState() != null ? found.getState() : "PENDING");
            
            // Update cache with fresh data from Kafka (write-through cache pattern)
            String cacheKey = CACHE_KEY_PREFIX + found.getCountry() + ":" + found.getTweetId() + ":" + found.getId();
            cacheService.put(cacheKey, message);
            // Also update CACHE_KEY_ALL
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> allMessages = (List<Map<String, Object>>) cacheService.get(CACHE_KEY_ALL, List.class);
            if (allMessages == null) {
                allMessages = new java.util.ArrayList<>();
            }
            // Remove old entry if exists
            allMessages.removeIf(msg -> {
                Object msgId = msg.get("id");
                return msgId != null && (msgId instanceof Number ? ((Number) msgId).longValue() == id : msgId.toString().equals(String.valueOf(id)));
            });
            // Add updated entry
            allMessages.add(message);
            cacheService.put(CACHE_KEY_ALL, allMessages);
            
            return ResponseEntity.ok(message);
        } catch (Exception ex) {
            log.error("Error getting message via Kafka: id={}", id, ex);
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
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 3);

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

            // Update cache with fresh data
            String cacheKey = CACHE_KEY_PREFIX + country + ":" + tweetId + ":" + id;
            cacheService.put(cacheKey, message);
            // Update CACHE_KEY_ALL
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> allMessages = (List<Map<String, Object>>) cacheService.get(CACHE_KEY_ALL, List.class);
            if (allMessages == null) {
                allMessages = new java.util.ArrayList<>();
            }
            // Remove old entry if exists
            allMessages.removeIf(msg -> {
                Object msgId = msg.get("id");
                return msgId != null && (msgId instanceof Number ? ((Number) msgId).longValue() == id : msgId.toString().equals(String.valueOf(id)));
            });
            // Add updated entry
            allMessages.add(message);
            cacheService.put(CACHE_KEY_ALL, allMessages);
            // Also invalidate pattern cache
            cacheService.deletePattern(CACHE_KEY_PREFIX + country + ":" + tweetId + ":*");

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
            KafkaMessageResponse getResponse = kafkaConsumer.waitForResponse(getRequestId, 3);

            if (getResponse == null || getResponse.getError() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            List<KafkaMessageResponse> kafkaMessages = getResponse.getMessages();
            if (kafkaMessages == null || kafkaMessages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
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
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 3);

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

            // Update cache with fresh data
            String cacheKey = CACHE_KEY_PREFIX + country + ":" + tweetId + ":" + id;
            cacheService.put(cacheKey, message);
            // Update CACHE_KEY_ALL
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> allMessages = (List<Map<String, Object>>) cacheService.get(CACHE_KEY_ALL, List.class);
            if (allMessages == null) {
                allMessages = new java.util.ArrayList<>();
            }
            // Remove old entry if exists
            allMessages.removeIf(msg -> {
                Object msgId = msg.get("id");
                return msgId != null && (msgId instanceof Number ? ((Number) msgId).longValue() == id : msgId.toString().equals(String.valueOf(id)));
            });
            // Add updated entry
            allMessages.add(message);
            cacheService.put(CACHE_KEY_ALL, allMessages);
            // Also invalidate pattern cache
            cacheService.deletePattern(CACHE_KEY_PREFIX + country + ":" + tweetId + ":*");

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

            // Remove from cache
            String cacheKey = CACHE_KEY_PREFIX + country + ":" + tweetId + ":" + id;
            cacheService.delete(cacheKey);
            // Invalidate all messages cache
            cacheService.delete(CACHE_KEY_ALL);
            cacheService.deletePattern(CACHE_KEY_PREFIX + country + ":" + tweetId + ":*");

            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            log.error("Error deleting message via Kafka", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegacy(@PathVariable("id") Long id) {
        try {
            // Try to get from cache first
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cached = (List<Map<String, Object>>) cacheService.get(CACHE_KEY_ALL, List.class);
            
            if (cached != null) {
                Map<String, Object> found = cached.stream()
                    .filter(msg -> {
                        Object msgId = msg.get("id");
                        return msgId != null && (msgId instanceof Number ? ((Number) msgId).longValue() == id : msgId.toString().equals(String.valueOf(id)));
                    })
                    .findFirst()
                    .orElse(null);
                
                if (found != null) {
                    String country = (String) found.get("country");
                    Object tweetIdObj = found.get("tweetId");
                    Long tweetId = tweetIdObj instanceof Number ? ((Number) tweetIdObj).longValue() : null;
                    
                    if (country != null && tweetId != null) {
                        // Delete using found country and tweetId
                        KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
                        kafkaRequest.setOperation("DELETE");
                        kafkaRequest.setCountry(country);
                        kafkaRequest.setTweetId(tweetId);
                        kafkaRequest.setId(id);

                        String requestId = kafkaProducer.sendMessage(kafkaRequest);
                        KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 2);

                        if (response == null) {
                            log.warn("Timeout waiting for DELETE response: id={}", id);
                            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
                        }

                        if (response.getError() != null) {
                            log.error("Error in DELETE response: {}", response.getError());
                            if (response.getError().contains("not found") || response.getError().contains("Not found")) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                            }
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }

                        // Remove from cache
                        String cacheKey = CACHE_KEY_PREFIX + country + ":" + tweetId + ":" + id;
                        cacheService.delete(cacheKey);
                        cacheService.delete(CACHE_KEY_ALL);
                        cacheService.deletePattern(CACHE_KEY_PREFIX + country + ":" + tweetId + ":*");

                        return ResponseEntity.noContent().build();
                    }
                }
            }
            
            // If not in cache, get all messages via Kafka
            KafkaMessageRequest getRequest = new KafkaMessageRequest();
            getRequest.setOperation("GET");

            String getRequestId = kafkaProducer.sendMessage(getRequest);
            KafkaMessageResponse getResponse = kafkaConsumer.waitForResponse(getRequestId, 2);

            if (getResponse == null) {
                log.warn("Timeout waiting for GET response to find message: id={}, trying DELETE with default country", id);
                // Try to delete with default country, as message might exist but GET timed out
                // This is a fallback - if message doesn't exist, DELETE will return error
                KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
                kafkaRequest.setOperation("DELETE");
                kafkaRequest.setCountry("default");
                // We need to find tweetId - try to get it from a recent message or use null
                // Discussion will need to find the message by ID alone
                kafkaRequest.setTweetId(null);
                kafkaRequest.setId(id);

                String requestId = kafkaProducer.sendMessage(kafkaRequest);
                KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 3);

                if (response == null) {
                    log.warn("Timeout waiting for DELETE response: id={}, assuming not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                if (response.getError() != null) {
                    log.error("Error in DELETE response: {}", response.getError());
                    if (response.getError().contains("not found") || response.getError().contains("Not found")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

                // Invalidate cache
                cacheService.delete(CACHE_KEY_ALL);
                cacheService.deletePattern(CACHE_KEY_PREFIX + "*");

                return ResponseEntity.noContent().build();
            }

            if (getResponse.getError() != null) {
                log.error("Error in GET response: {}", getResponse.getError());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            List<KafkaMessageResponse> kafkaMessages = getResponse.getMessages();
            if (kafkaMessages == null || kafkaMessages.isEmpty()) {
                log.warn("No messages found in response for DELETE: id={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            KafkaMessageResponse found = kafkaMessages.stream()
                .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                .findFirst()
                .orElse(null);

            if (found == null) {
                log.warn("Message not found for DELETE: id={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String country = found.getCountry();
            Long tweetId = found.getTweetId();

            if (country == null || tweetId == null) {
                log.error("Invalid message data for DELETE: id={}, country={}, tweetId={}", id, country, tweetId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Now delete
            KafkaMessageRequest kafkaRequest = new KafkaMessageRequest();
            kafkaRequest.setOperation("DELETE");
            kafkaRequest.setCountry(country);
            kafkaRequest.setTweetId(tweetId);
            kafkaRequest.setId(id);

            String requestId = kafkaProducer.sendMessage(kafkaRequest);
            KafkaMessageResponse response = kafkaConsumer.waitForResponse(requestId, 3);

            if (response == null) {
                log.warn("Timeout waiting for DELETE response: id={}", id);
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            if (response.getError() != null) {
                log.error("Error in DELETE response: {}", response.getError());
                // Check if error is "not found"
                if (response.getError().contains("not found") || response.getError().contains("Not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Remove from cache
            String cacheKey = CACHE_KEY_PREFIX + country + ":" + tweetId + ":" + id;
            cacheService.delete(cacheKey);
            // Invalidate all messages cache
            cacheService.delete(CACHE_KEY_ALL);
            cacheService.deletePattern(CACHE_KEY_PREFIX + country + ":" + tweetId + ":*");

            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            log.error("Error deleting message via Kafka: id={}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
