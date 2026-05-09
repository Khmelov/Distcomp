package com.distcomp.service.note;

import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteProxyService {

    private final WebClient webClient;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private String noteCacheKey(final Long id) {
        return "proxy:note:" + id;
    }

    private String listCacheKey(final Long topicId, final int page, final int size) {
        return "proxy:notes:topic:" + topicId + ":page:" + page + ":size:" + size;
    }

    private String allListCacheKey(final int page, final int size) {
        return "proxy:notes:all:page:" + page + ":size:" + size;
    }

    private Mono<Void> evictNoteCache(final Long id) {
        return redisTemplate.delete(noteCacheKey(id)).then();
    }

    private Mono<Void> evictTopicListCaches(final Long topicId) {
        final String pattern = "proxy:notes:topic:" + topicId + ":*";
        return redisTemplate.scan(ScanOptions.scanOptions().match(pattern).build())
                .doOnNext(key -> log.debug("Evicting topic list cache: {}", key))
                .flatMap(redisTemplate::delete)
                .then();
    }

    private Mono<Void> evictAllNotesListCaches() {
        final String pattern = "proxy:notes:all:*";
        return redisTemplate.scan(ScanOptions.scanOptions().match(pattern).build())
                .doOnNext(key -> log.debug("Evicting all list cache: {}", key))
                .flatMap(redisTemplate::delete)
                .then();
    }

    public Mono<NoteResponseDto> getNoteById(final Long id) {
        final String cacheKey = noteCacheKey(id);
        return redisTemplate.opsForValue().get(cacheKey)
                .map(obj -> objectMapper.convertValue(obj, NoteResponseDto.class))
                .switchIfEmpty(webClient.get().uri("/api/v1.0/notes/{id}", id)
                        .retrieve().bodyToMono(NoteResponseDto.class)
                        .flatMap(note -> redisTemplate.opsForValue().set(cacheKey, note, CACHE_TTL).thenReturn(note)));
    }

    public Flux<NoteResponseDto> getAllNotes(final int page, final int size) {
        final String cacheKey = allListCacheKey(page, size);
        return redisTemplate.opsForValue().get(cacheKey)
                .map(obj -> objectMapper.convertValue(obj, new TypeReference<List<NoteResponseDto>>() {}))
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(getFromUpstreamAndCache(page, size, cacheKey));
    }

    private Flux<NoteResponseDto> getFromUpstreamAndCache(final int page, final int size, final String cacheKey) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1.0/notes")
                        .queryParam("page", page)
                        .queryParam("size", size).build())
                .retrieve()
                .bodyToFlux(NoteResponseDto.class)
                .collectList()
                .doOnSuccess(list -> log.debug("Caching list for key {}: size {}", cacheKey, list.size()))
                .flatMapMany(list -> redisTemplate.opsForValue()
                        .set(cacheKey, list, CACHE_TTL)
                        .thenMany(Flux.fromIterable(list)));
    }

    public Flux<NoteResponseDto> getNotesByTopicId(final Long topicId, final int page, final int size) {
        final String cacheKey = listCacheKey(topicId, page, size);
        return redisTemplate.opsForValue().get(cacheKey)
                .map(obj -> objectMapper.convertValue(obj, new TypeReference<List<NoteResponseDto>>() {}))
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(webClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/api/v1.0/notes")
                                .queryParam("topicId", topicId)
                                .queryParam("page", page)
                                .queryParam("size", size).build())
                        .retrieve()
                        .bodyToFlux(NoteResponseDto.class)
                        .collectList()
                        .flatMapMany(list -> redisTemplate.opsForValue()
                                .set(cacheKey, list, CACHE_TTL)
                                .thenMany(Flux.fromIterable(list))));
    }

    public Mono<NoteResponseDto> createNote(final NoteCreateRequest request) {
        return webClient.post().uri("/api/v1.0/notes").bodyValue(request).retrieve().bodyToMono(NoteResponseDto.class)
                .flatMap((final NoteResponseDto note) -> {
                    log.debug("Created note {}. Evicting caches.", note.getId());
                    return evictTopicListCaches(note.getTopicId())
                            .then(evictAllNotesListCaches())
                            .thenReturn(note);
                });
    }

    public Mono<NoteResponseDto> updateNote(final Long id, final NoteUpdateRequest request) {
        return webClient.put().uri("/api/v1.0/notes/{id}", id).bodyValue(request).retrieve().bodyToMono(NoteResponseDto.class)
                .flatMap((final NoteResponseDto note) -> {
                    log.debug("Updated note {}. Evicting caches.", note.getId());
                    return evictNoteCache(note.getId())
                            .then(evictTopicListCaches(note.getTopicId()))
                            .then(evictAllNotesListCaches())
                            .thenReturn(note);
                });
    }

    public Mono<NoteResponseDto> patchNote(final Long id, final NotePatchRequest request) {
        return webClient.patch().uri("/api/v1.0/notes/{id}", id).bodyValue(request).retrieve().bodyToMono(NoteResponseDto.class)
                .flatMap((final NoteResponseDto note) -> {
                    log.debug("Patched note {}. Evicting caches.", note.getId());
                    return evictNoteCache(note.getId())
                            .then(evictTopicListCaches(note.getTopicId()))
                            .then(evictAllNotesListCaches())
                            .thenReturn(note);
                });
    }

    public Mono<Void> deleteNotesByTopicId(final Long topicId) {
        return webClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1.0/notes").queryParam("topicId", topicId).build())
                .retrieve().bodyToMono(Void.class)
                .then(evictTopicListCaches(topicId))
                .then(evictAllNotesListCaches());
    }

    public Mono<Void> deleteNote(final Long id) {
        return webClient.delete().uri("/api/v1.0/notes/{id}", id).retrieve().bodyToMono(Void.class)
                .then(evictNoteCache(id))
                .then(evictAllNotesListCaches());
    }
}