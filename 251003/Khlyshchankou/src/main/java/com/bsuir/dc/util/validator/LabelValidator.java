package com.bsuir.dc.util.validator;

import com.bsuir.dc.dto.request.LabelRequestTo;
import com.bsuir.dc.dto.request.TopicRequestTo;
import com.bsuir.dc.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LabelValidator implements Validator {
    private final LabelService labelService;

    @Autowired
    public LabelValidator(LabelService labelService) {
        this.labelService = labelService;
    }

    @Override
    public boolean supports(Class<?> clazz) { return TopicRequestTo.class.equals(clazz); }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            LabelRequestTo label = (LabelRequestTo) target;
            if (labelService.existsByName(label.getName())){
                errors.rejectValue("title", null, "Label with such name already exists");
            }
        }
    }
}
