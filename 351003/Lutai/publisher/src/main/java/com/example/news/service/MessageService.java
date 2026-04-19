package com.example.news.service;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final RestTemplate restTemplate;
    // Адрес твоего DiscussionService (Cassandra микросервис)
    private final String DISCUSSION_URL = "http://localhost:24130/api/v1.0/messages";

    public MessageResponseTo create(MessageRequestTo request) {
        return restTemplate.postForObject(DISCUSSION_URL, request, MessageResponseTo.class);
    }

    public List<MessageResponseTo> findAll(int page, int size, String sortBy) {
        // Получаем массив и превращаем в список
        MessageResponseTo[] response = restTemplate.getForObject(DISCUSSION_URL, MessageResponseTo[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    public MessageResponseTo findById(Long id) {
        return restTemplate.getForObject(DISCUSSION_URL + "/" + id, MessageResponseTo.class);
    }

    public void delete(Long id) {
        restTemplate.delete(DISCUSSION_URL + "/" + id);
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        restTemplate.put(DISCUSSION_URL + "/" + id, request);
        return findById(id);
    }
}