package by.molchan.Publisher.utils;

import by.molchan.Publisher.DTOs.Requests.CreatorRequestDTO;
import by.molchan.Publisher.services.CreatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreatorValidator implements Validator {
    private final CreatorsService creatorsService;

    @Autowired
    public CreatorValidator(CreatorsService creatorsService) {
        this.creatorsService = creatorsService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return CreatorRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            CreatorRequestDTO creator = (CreatorRequestDTO) target;
            if (creatorsService.existsByLogin(creator.getLogin())){
                errors.rejectValue("login", null, "Creator with such login already exists");
            }
        }
    }
}
