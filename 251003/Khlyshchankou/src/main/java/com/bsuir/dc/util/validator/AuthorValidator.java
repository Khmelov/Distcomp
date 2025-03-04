package com.bsuir.dc.util.validator;

import com.bsuir.dc.dto.request.AuthorRequestTo;
import com.bsuir.dc.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AuthorValidator implements Validator {
    private final AuthorService authorService;

    @Autowired
    public AuthorValidator(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public boolean supports(Class<?> clazz) { return AuthorRequestTo.class.equals(clazz); }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            AuthorRequestTo creator = (AuthorRequestTo) target;
            if (authorService.existsByLogin(creator.getLogin())){
                errors.rejectValue("login", null, "Author with such login already exists");
            }
        }
    }
}