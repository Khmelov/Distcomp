package com.github.Lexya06.startrestapp.controller.realization;

import com.github.Lexya06.startrestapp.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.model.dto.request.LabelRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.LabelResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.realization.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
