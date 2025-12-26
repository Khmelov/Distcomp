package com.restApp.mapper;

import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.AuthorResponseTo;
import com.restApp.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequestTo request) {
        Author author = new Author();
        author.setLogin(request.getLogin());
        author.setPassword(request.getPassword());
        author.setFirstname(request.getFirstname());
        author.setLastname(request.getLastname());
        return author;
    }

    public AuthorResponseTo toResponse(Author entity) {
        AuthorResponseTo response = new AuthorResponseTo();
        response.setId(entity.getId());
        response.setLogin(entity.getLogin());
        response.setFirstname(entity.getFirstname());
        response.setLastname(entity.getLastname());
        return response;
    }

    public void updateEntity(Author entity, AuthorRequestTo request) {
        if (request.getLogin() != null)
            entity.setLogin(request.getLogin());
        if (request.getPassword() != null)
            entity.setPassword(request.getPassword());
        if (request.getFirstname() != null)
            entity.setFirstname(request.getFirstname());
        if (request.getLastname() != null)
            entity.setLastname(request.getLastname());
    }
}
