package com.github.Lexya06.startrestapp.controller.realization;

import com.github.Lexya06.startrestapp.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.model.dto.request.LabelRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.ArticleResponseTo;
import com.github.Lexya06.startrestapp.model.dto.response.LabelResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.realization.LabelService;
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
