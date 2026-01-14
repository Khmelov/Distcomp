package com.blog.client;

import com.blog.dto.response.MessageResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Component
public class DiscussionClient {

    @Value("${discussion.service.url:http://localhost:24130}")
    private String discussionServiceUrl;

    private final RestTemplate restTemplate;

    public DiscussionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MessageResponseTo getMessage(Long id) {
        try {
            String url = discussionServiceUrl + "/api/v1.0/messages/" + id;

            // Настраиваем заголовки
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<MessageResponseTo> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    MessageResponseTo.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                MessageResponseTo message = response.getBody();
                // Если Discussion вернул NOT_FOUND, возвращаем null
                if ("NOT_FOUND".equals(message.getState())) {
                    return null;
                }
                return message;
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            // Discussion вернул 404
            return null;
        } catch (HttpClientErrorException e) {
            // Другие HTTP ошибки
            System.err.println("HTTP error fetching message " + id + ": " + e.getStatusCode() + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            // Общие ошибки
            System.err.println("Error fetching message from discussion for id " + id + ": " + e.getMessage());
            return null;
        }
    }

    public List<MessageResponseTo> getAllMessages() {
        try {
            String url = discussionServiceUrl + "/api/v1.0/messages";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<MessageResponseTo[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    MessageResponseTo[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return List.of();
        } catch (Exception e) {
            System.err.println("Error fetching all messages from discussion: " + e.getMessage());
            return List.of();
        }
    }

    // Дополнительный метод для тестирования соединения
    public boolean isAvailable() {
        try {
            String url = discussionServiceUrl + "/api/v1.0/messages";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}