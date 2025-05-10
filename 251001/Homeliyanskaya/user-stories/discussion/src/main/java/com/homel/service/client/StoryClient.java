package com.homel.service.client;

import com.homel.dto.StoryResponseTo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StoryClient {
    private final RestTemplate restTemplate;

    public StoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public StoryResponseTo getStoryById(Long storyId) {
        // Здесь указывается URL для запроса к внешнему сервису
        String url = "http://localhost:24110/api/v1.0/storys/" + storyId;
        return restTemplate.getForObject(url, StoryResponseTo.class);
    }
}

