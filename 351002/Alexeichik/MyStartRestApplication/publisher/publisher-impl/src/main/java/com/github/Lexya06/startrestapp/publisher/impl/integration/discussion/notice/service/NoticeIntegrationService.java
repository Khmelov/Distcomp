package com.github.Lexya06.startrestapp.publisher.impl.integration.discussion.notice.service;

import com.github.Lexya06.startrestapp.discussion.api.dto.PagedResponse;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeKeyDto;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeRequestTo;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeResponseTo;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation.NoticeSearchCriteria;
import com.github.Lexya06.startrestapp.publisher.impl.integration.discussion.notice.client.NoticeClient;
import com.github.Lexya06.startrestapp.publisher.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.publisher.impl.service.realization.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class NoticeIntegrationService {
    private final ArticleService articleService;
    private final NoticeClient noticeClient;


    @Autowired
    public NoticeIntegrationService(NoticeClient noticeClient, ArticleService articleService) {
        this.noticeClient = noticeClient;
        this.articleService = articleService;
    }

    public Mono<NoticeResponseTo> getById(NoticeKeyDto id) {
        return noticeClient.getById(id);
    }

    public Mono<NoticeResponseTo> create(NoticeRequestTo requestDTO) {
        try {
            articleService.getEntityById(requestDTO.getArticleId());
        }
        catch (Exception e) {
            throw new MyEntityNotFoundException(requestDTO.getArticleId(), articleService.getEntityClass());
        }
        return noticeClient.create(requestDTO);
    }

    public Mono<NoticeResponseTo> update(NoticeKeyDto id, NoticeRequestTo requestDTO) {
        return noticeClient.update(id, requestDTO);
    }

    public Mono<Void> delete(NoticeKeyDto id) {
        return noticeClient.delete(id);
    }

    public Mono<ResponseEntity<List<NoticeResponseTo>>> getAllByCriteria(NoticeSearchCriteria criteria) {
        return noticeClient.getAll(criteria);
    }

    public Mono<NoticeResponseTo> getByIdId(Long id) {
        return noticeClient.getByIdId(id);
    }

    public Mono<NoticeResponseTo> updateByIdId(Long id, NoticeRequestTo requestDTO) {
        return noticeClient.updateByIdId(id, requestDTO);
    }

    public Mono<ResponseEntity<Void>> deleteByIdId(Long id) {
        return noticeClient.deleteByIdId(id);
    }
}
