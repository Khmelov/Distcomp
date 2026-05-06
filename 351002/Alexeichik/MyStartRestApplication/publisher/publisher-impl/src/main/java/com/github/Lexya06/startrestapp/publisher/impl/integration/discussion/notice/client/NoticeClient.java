package com.github.Lexya06.startrestapp.publisher.impl.integration.discussion.notice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeKeyDto;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeRequestTo;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeResponseTo;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation.NoticeSearchCriteria;
import com.github.Lexya06.startrestapp.publisher.impl.integration.client.abstraction.BaseClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NoticeClient extends BaseClient<NoticeKeyDto, NoticeRequestTo, NoticeResponseTo, NoticeSearchCriteria> {

    private static final String NOTICES_PATH = "/notices";

    public NoticeClient(@Qualifier("discussionWebClient") WebClient discussionWebClient, ObjectMapper objectMapper) {
        super(
                discussionWebClient,
                NOTICES_PATH,
                NoticeResponseTo.class,
                objectMapper
        );
    }

    public Mono<NoticeResponseTo> getByIdId(Long id) {
        return webClient.get()
                .uri(basePath + "/{id}", id)
                .retrieve()
                .bodyToMono(responseClass);
    }

    public Mono<NoticeResponseTo> updateByIdId(Long id, NoticeRequestTo requestDTO) {
        return webClient.put()
                .uri(basePath + "/{id}", id)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(responseClass);
    }

    public Mono<ResponseEntity<Void>> deleteByIdId(Long id) {
        return webClient.delete()
                .uri(basePath + "/{id}", id)
                .retrieve()
                .toEntity(Void.class);
    }
}