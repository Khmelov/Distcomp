package com.github.Lexya06.startrestapp.publisher.impl.controller.realization.v2;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeKeyDto;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeRequestTo;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeResponseTo;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation.NoticeSearchCriteria;
import com.github.Lexya06.startrestapp.publisher.impl.integration.discussion.notice.service.NoticeIntegrationService;
import com.github.Lexya06.startrestapp.publisher.impl.service.realization.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/notices")
@Validated
@RequiredArgsConstructor
public class NoticeIntegrationControllerV2 {

    private final NoticeIntegrationService integrationService;
    private final ArticleService articleService;

    @GetMapping("/by-key/{id}")
    public Mono<NoticeResponseTo> getNotice(@PathVariable NoticeKeyDto id) {
        return integrationService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NoticeResponseTo> createNotice(@Valid @RequestBody NoticeRequestTo requestDTO) {
        return integrationService.create(requestDTO);
    }

    @PutMapping("/by-key/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @articleService.getEntityById(#id.articleId).userId == principal.id)")
    public Mono<NoticeResponseTo> updateNotice(@PathVariable NoticeKeyDto id, @Valid @RequestBody NoticeRequestTo requestDTO) {
        return integrationService.update(id, requestDTO);
    }

    @DeleteMapping("/by-key/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @articleService.getEntityById(#id.articleId).userId == principal.id)")
    public Mono<Void> deleteNotice(@PathVariable NoticeKeyDto id) {
        return integrationService.delete(id);
    }

    @GetMapping
    public Mono<ResponseEntity<List<NoticeResponseTo>>> getAllNotices(@Valid @ModelAttribute NoticeSearchCriteria criteria) {
        return integrationService.getAllByCriteria(criteria);
    }

    @GetMapping("/{id}")
    public Mono<NoticeResponseTo> getNotice(@PathVariable Long id) {
        return integrationService.getByIdId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @articleService.getEntityById(integrationService.getByIdId(#id).block().articleId).userId == principal.id)")
    public Mono<NoticeResponseTo> updateNotice(@PathVariable Long id, @Valid @RequestBody NoticeRequestTo requestDTO) {
        return integrationService.updateByIdId(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @articleService.getEntityById(integrationService.getByIdId(#id).block().articleId).userId == principal.id)")
    public Mono<ResponseEntity<Void>> deleteNotice(@PathVariable Long id) {
        return integrationService.deleteByIdId(id);
    }
}
