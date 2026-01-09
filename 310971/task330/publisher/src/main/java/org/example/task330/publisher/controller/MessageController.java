package org.example.task330.publisher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final WebClient discussionWebClient;

    public MessageController(WebClient discussionWebClient) {
        this.discussionWebClient = discussionWebClient;
    }

    @GetMapping
    public ResponseEntity<List> getAll(@RequestParam(required = false) String country,
                                       @RequestParam(required = false) Long tweetId) {
        try {
            List response = discussionWebClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/api/v1.0/messages");
                        if (country != null && tweetId != null) {
                            uriBuilder.queryParam("country", country)
                                    .queryParam("tweetId", tweetId);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
            
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Map> getById(@PathVariable("country") String country,
                                       @PathVariable("tweetId") Long tweetId,
                                       @PathVariable("id") Long id) {
        try {
            Map response = discussionWebClient.get()
                    .uri("/api/v1.0/messages/{country}/{tweetId}/{id}", country, tweetId, id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Legacy format support: GET /api/v1.0/messages/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map> getByIdLegacy(@PathVariable("id") Long id) {
        try {
            // Try to find message by searching in all messages
            // First try default country
            List<Map> allMessages = discussionWebClient.get()
                    .uri("/api/v1.0/messages")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
            
            if (allMessages != null) {
                Map found = allMessages.stream()
                        .filter(msg -> {
                            Object msgId = msg.get("id");
                            return msgId != null && msgId.toString().equals(id.toString());
                        })
                        .findFirst()
                        .orElse(null);
                
                if (found != null) {
                    // Return the found message
                    return ResponseEntity.ok(found);
                }
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map> create(@RequestBody Map<String, Object> request) {
        try {
            // Add default country if not provided
            if (!request.containsKey("country") || request.get("country") == null) {
                request.put("country", "default");
            }
            
            Map response = discussionWebClient.post()
                    .uri("/api/v1.0/messages")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Map> update(@PathVariable("country") String country,
                                      @PathVariable("tweetId") Long tweetId,
                                      @PathVariable("id") Long id,
                                      @RequestBody Map<String, Object> request) {
        try {
            Map response = discussionWebClient.put()
                    .uri("/api/v1.0/messages/{country}/{tweetId}/{id}", country, tweetId, id)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Legacy format support: PUT /api/v1.0/messages/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Map> updateLegacy(@PathVariable("id") Long id,
                                            @RequestBody Map<String, Object> request) {
        try {
            // Extract country and tweetId from request or find message first
            String country = (String) request.get("country");
            Long tweetId = request.get("tweetId") != null ? 
                    ((Number) request.get("tweetId")).longValue() : null;
            
            // If country or tweetId not provided, try to find message first
            if (country == null || tweetId == null) {
                List<Map> allMessages = discussionWebClient.get()
                        .uri("/api/v1.0/messages")
                        .retrieve()
                        .bodyToMono(List.class)
                        .block();
                
                if (allMessages != null) {
                    Map found = allMessages.stream()
                            .filter(msg -> {
                                Object msgId = msg.get("id");
                                return msgId != null && msgId.toString().equals(id.toString());
                            })
                            .findFirst()
                            .orElse(null);
                    
                    if (found != null) {
                        country = country != null ? country : (String) found.get("country");
                        tweetId = tweetId != null ? tweetId : ((Number) found.get("tweetId")).longValue();
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            }
            
            // Ensure country is set
            if (country == null) {
                country = "default";
            }
            
            // Update request with country if not present
            if (!request.containsKey("country")) {
                request.put("country", country);
            }
            
            Map response = discussionWebClient.put()
                    .uri("/api/v1.0/messages/{country}/{tweetId}/{id}", country, tweetId, id)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Void> delete(@PathVariable("country") String country,
                                        @PathVariable("tweetId") Long tweetId,
                                        @PathVariable("id") Long id) {
        try {
            discussionWebClient.delete()
                    .uri("/api/v1.0/messages/{country}/{tweetId}/{id}", country, tweetId, id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            
            return ResponseEntity.noContent().build();
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Legacy format support: DELETE /api/v1.0/messages/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegacy(@PathVariable("id") Long id) {
        try {
            // Find message first to get country and tweetId
            List<Map> allMessages = discussionWebClient.get()
                    .uri("/api/v1.0/messages")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
            
            if (allMessages != null) {
                Map found = allMessages.stream()
                        .filter(msg -> {
                            Object msgId = msg.get("id");
                            return msgId != null && msgId.toString().equals(id.toString());
                        })
                        .findFirst()
                        .orElse(null);
                
                if (found != null) {
                    String country = (String) found.get("country");
                    Long tweetId = ((Number) found.get("tweetId")).longValue();
                    
                    discussionWebClient.delete()
                            .uri("/api/v1.0/messages/{country}/{tweetId}/{id}", country, tweetId, id)
                            .retrieve()
                            .toBodilessEntity()
                            .block();
                    
                    return ResponseEntity.noContent().build();
                }
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (WebClientResponseException ex) {
            log.error("Error calling discussion service: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (Exception ex) {
            log.error("Unexpected error calling discussion service", ex);
            if (ex.getCause() instanceof ConnectException || 
                ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

