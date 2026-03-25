package com.github.Lexya06.startrestapp.controller.realization;

import com.github.Lexya06.startrestapp.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.model.dto.request.NoticeRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.NoticeResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.realization.NoticeService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${server.api.base-path.v1}/notices")
@Validated
public class NoticeController extends BaseController<Notice, NoticeRequestTo, NoticeResponseTo> {
    NoticeService noticeService;
    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }
    @Override
    protected BaseEntityService<Notice, NoticeRequestTo, NoticeResponseTo> getBaseService() {
        return noticeService;
    }

    @Override
    public ResponseEntity<List<NoticeResponseTo>> getAllEntities(@QuerydslPredicate(root = Notice.class) Predicate predicate, Pageable pageable) {
        return getAllEntitiesBase(predicate, pageable);
    }
}
