package com.blog.client;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


import java.util.Collections;
import java.util.List;

@Component
public class DiscussionClient {

    private final RestClient restClient;



    public DiscussionClient(@Value("${discussion.service.url:http://localhost:24130}") String discussionUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(discussionUrl)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new RestClientException("Discussion service error: " + response.getStatusCode());
                })
                .build();
    }

    public List<MessageResponseTo> getAllMessages() {
        try {
            return restClient.get()
                    .uri("/api/v1.0/messages")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MessageResponseTo>>() {});
        } catch (RestClientException e) {
            // Если ошибка, возвращаем пустой список
            System.err.println("Error getting all messages: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Получить сообщения по топику
    public List<MessageResponseTo> getMessagesByTopic(Long topicId, String country) {
        return restClient.get()
                .uri("/api/v1.0/messages/topic/{topicId}?country={country}", topicId, country)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MessageResponseTo>>() {});
    }

    public MessageResponseTo getMessage(Long id, Long topicId, String country) {
        System.out.println("DiscussionClient.getMessage() - id: " + id + ", topicId: " + topicId + ", country: " + country);

        try {
            // Строим URL в зависимости от наличия параметров
            String url;
            Object[] params;

            if (topicId != null) {
                url = "/api/v1.0/messages/{id}?topicId={topicId}&country={country}";
                params = new Object[]{id, topicId, country};
            } else {
                url = "/api/v1.0/messages/{id}?country={country}";
                params = new Object[]{id, country};
            }

            System.out.println("Calling Discussion: " + url);

            // Используем RestClient с обработкой ошибок
            return restClient.get()
                    .uri(url, params)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                        // Если 404, не бросаем исключение, а возвращаем null
                        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                            throw new RestClientException("Message not found");
                        }
                        throw new RestClientException("Client error: " + response.getStatusCode());
                    })
                    .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                        throw new RestClientException("Server error: " + response.getStatusCode());
                    })
                    .body(MessageResponseTo.class);

        } catch (RestClientException e) {
            System.err.println("Discussion service error: " + e.getMessage());
            throw e; // Пробрасываем выше для обработки в контроллере
        }
    }


    // Создать сообщение
    public MessageResponseTo createMessage(String country, MessageRequestTo request) {
        return restClient.post()
                .uri("/api/v1.0/messages?country={country}", country)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(MessageResponseTo.class);
    }

    // Обновить сообщение
    public MessageResponseTo updateMessage(Long id, Long topicId, String country, MessageRequestTo request) {
        return restClient.put()
                .uri("/api/v1.0/messages/{id}?topicId={topicId}&country={country}", id, topicId, country)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(MessageResponseTo.class);
    }

    // Удалить сообщение
    public void deleteMessage(Long id, Long topicId, String country) {
        restClient.delete()
                .uri("/api/v1.0/messages/{id}?topicId={topicId}&country={country}", id, topicId, country)
                .retrieve()
                .toBodilessEntity();
    }

    // Проверить существование сообщения
    public boolean existsMessage(Long id, Long topicId, String country) {
        return Boolean.TRUE.equals(restClient.get()
                .uri("/api/v1.0/messages/{id}/exists?topicId={topicId}&country={country}", id, topicId, country)
                .retrieve()
                .body(Boolean.class));
    }
}