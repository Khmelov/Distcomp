package com.bsuir.dc.util.validator;

import com.bsuir.dc.dto.request.TopicRequestTo;
import com.bsuir.dc.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TopicValidator implements Validator {
    private final TopicService topicService;

    @Autowired
    public TopicValidator(TopicService topicService) {
        this.topicService = topicService;
    }

    @Override
    public boolean supports(Class<?> clazz) { return TopicRequestTo.class.equals(clazz); }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            TopicRequestTo topic = (TopicRequestTo) target;
            if (topicService.existsByTitle(topic.getTitle())){
                errors.rejectValue("title", null, "Topic with such title already exists");
            }
        }
    }
}
