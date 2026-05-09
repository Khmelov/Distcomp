package com.github.Lexya06.startrestapp.discussion.impl.controller.realization;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeKeyDto;
import com.github.Lexya06.startrestapp.discussion.impl.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeRequestTo;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeResponseTo;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation.NoticeSearchCriteria;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.NoticeKey;
import com.github.Lexya06.startrestapp.discussion.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.discussion.impl.service.realization.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${server.api.base-path.v1}/notices")
@Validated
public class NoticeController extends BaseController<Notice, NoticeKey, NoticeKeyDto, NoticeRequestTo, NoticeResponseTo, NoticeSearchCriteria> {
    NoticeService noticeService;
    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }
    @Override
    protected BaseEntityService<Notice, NoticeKey, NoticeKeyDto, NoticeRequestTo, NoticeResponseTo, NoticeSearchCriteria> getBaseService() {
        return noticeService;
    }

    @Override
    public ResponseEntity<NoticeResponseTo> createEntity(@Valid @RequestBody NoticeRequestTo requestDTO) {
        throw new UnsupportedOperationException("Creation of notices is now handled by the Publisher service via Kafka.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getByIdId(@PathVariable Long id){
        return ResponseEntity.ok(noticeService.getEntityByIdId(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> updateEntityByIdId(@PathVariable Long id, @Valid @RequestBody NoticeRequestTo requestDTO){
        return ResponseEntity.ok(noticeService.updateEntityByIdId(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntityByIdId(@PathVariable Long id){
        noticeService.deleteEntityByIdId(id);
        return ResponseEntity.noContent().build();
    }
}
