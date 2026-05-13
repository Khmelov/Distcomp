package com.github.Lexya06.startrestapp.publisher.impl.integration.client.abstraction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.Lexya06.startrestapp.discussion.api.dto.PagedResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public abstract class BaseClient<K, RequestDTO, ResponseDTO, C> {

    protected final WebClient webClient;
    protected final String basePath;
    protected final Class<ResponseDTO> responseClass;
    protected final ObjectMapper objectMapper; // Добавили ObjectMapper

    protected BaseClient(WebClient webClient,
                         String basePath,
                         Class<ResponseDTO> responseClass,
                         ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.basePath = basePath;
        this.responseClass = responseClass;
        this.objectMapper = objectMapper;
    }

    public Mono<ResponseDTO> getById(K id) {
        return webClient.get()
                .uri(basePath + "/{id}", id)
                .retrieve()
                .bodyToMono(responseClass);
    }

    public Mono<ResponseDTO> create(RequestDTO requestDTO) {
        return webClient.post()
                .uri(basePath)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(responseClass);
    }

    public Mono<ResponseDTO> update(K id, RequestDTO requestDTO) {
        return webClient.put()
                .uri(basePath + "/{id}", id)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(responseClass);
    }

    public Mono<Void> delete(K id) {
        return webClient.delete()
                .uri(basePath + "/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<ResponseEntity<List<ResponseDTO>>> getAll(C criteria) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(basePath)
                        .queryParams(buildQueryParams(criteria))
                        .build())
                .retrieve().toEntityList(responseClass);
    }

    /**
     * Автоматически конвертирует объект критериев в Query параметры запроса.
     */
    protected MultiValueMap<String, String> buildQueryParams(C criteria) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        if (criteria == null) {
            return queryParams;
        }

        // Конвертируем POJO в Map<String, Object>
        Map<String, Object> map = objectMapper.convertValue(criteria, new TypeReference<>() {
        });

        // Перекладываем значения в MultiValueMap, игнорируя null
        map.forEach((key, value) -> {
            if (value != null) {
                queryParams.add(key, String.valueOf(value));
            }
        });

        return queryParams;
    }
}
