package com.example.task330.service.impl;

import com.example.task330.domain.dto.request.ReactionRequestTo;
import com.example.task330.domain.dto.response.ReactionResponseTo;
import com.example.task330.service.ReactionService;

import lombok.RequiredArgsConstructor;

import com.example.task330.repository.TweetRepository; // ДОБАВИТЬ
import com.example.task330.exception.EntityNotFoundException; // ДОБАВИТЬ
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import org.springframework.http.MediaType; // ДОБАВЬ ЭТУ СТРОКУ
import org.springframework.core.ParameterizedTypeReference;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final RestClient restClient;
    private final TweetRepository tweetRepository;

    @Autowired
    public ReactionServiceImpl(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0/reactions")
                .build();
    }

    @Override
    public ReactionResponseTo create(ReactionRequestTo request) { 
        if (!tweetRepository.existsById(request.getTweetId())) {
            throw new EntityNotFoundException("Cannot create reaction: Tweet not found with id " + request.getTweetId());
        }
        return restClient.post().body(request).retrieve().body(ReactionResponseTo.class);
    }

    @Override
    public List<ReactionResponseTo> findAll(int page, int size) {
        return restClient.get()
                .uri(uri -> uri.queryParam("page", page).queryParam("size", size).build())
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<ReactionResponseTo>>() {});
    }

    @Override
    public ReactionResponseTo findById(Long id) {
        return restClient.get().uri("/{id}", id).retrieve().body(ReactionResponseTo.class);
    }

    @Override
    public ReactionResponseTo update(ReactionRequestTo request) {
        return restClient.put()
                .uri("/{id}", request.getId()) // ДОБАВЛЯЕМ ID в URL
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ReactionResponseTo.class);
    }

    @Override
    public void deleteById(Long id) {
        restClient.delete()
                .uri("/{id}", id)
                .retrieve()
                // Если другой микросервис вернул 404, мы тоже выбрасываем исключение
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new EntityNotFoundException("Reaction not found");
                })
                .toBodilessEntity();
    }
}