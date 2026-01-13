package com.publick.service.mapper;

import com.publick.dto.AuthorRequestTo;
import com.publick.dto.AuthorResponseTo;
import com.publick.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequestTo dto) {
        if (dto == null) {
            return null;
        }
        Author author = new Author(dto.getLogin(), dto.getPassword(), dto.getFirstname(), dto.getLastname());
        return author;
    }

    public AuthorResponseTo toResponse(Author entity) {
        if (entity == null) {
            return null;
        }
        AuthorResponseTo response = new AuthorResponseTo();
        response.setId(entity.getId());
        response.setLogin(entity.getLogin());
        response.setPassword(entity.getPassword());
        response.setFirstname(entity.getFirstname());
        response.setLastname(entity.getLastname());
        return response;
    }

    public void updateEntityFromDto(AuthorRequestTo dto, Author entity) {
        if (dto != null && entity != null) {
            entity.setLogin(dto.getLogin());
            entity.setPassword(dto.getPassword());
            entity.setFirstname(dto.getFirstname());
            entity.setLastname(dto.getLastname());
        }
    }
}