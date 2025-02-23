package by.kapinskiy.Distcomp.utils;

import by.kapinskiy.Distcomp.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Requests.TagRequestDTO;
import by.kapinskiy.Distcomp.services.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TagValidator implements Validator {
    private final TagsService tagsService;

    @Autowired
    public TagValidator(TagsService tagsService) {
        this.tagsService = tagsService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return IssueRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            TagRequestDTO tag = (TagRequestDTO) target;
            if (tagsService.existsByName(tag.getName())){
                errors.rejectValue("title", null, "Tag with such name already exists");
            }
        }
    }
}
