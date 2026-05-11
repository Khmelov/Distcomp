package com.github.Lexya06.startrestapp.publisher.impl.controller.realization;

import com.github.Lexya06.startrestapp.publisher.impl.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.publisher.impl.service.realization.LabelService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${server.api.base-path.v1}/labels")
public class LabelController extends BaseController<Label, LabelRequestTo, LabelResponseTo> {
    LabelService labelService;
    @Autowired
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }
    @Override
    protected BaseEntityService<Label, LabelRequestTo, LabelResponseTo> getBaseService() {
        return labelService;
    }

    @Override
    public ResponseEntity<List<LabelResponseTo>> getAllEntities(@QuerydslPredicate(root = Label.class) Predicate predicate, Pageable pageable) {
        return getAllEntitiesBase(predicate, pageable);
    }


}
