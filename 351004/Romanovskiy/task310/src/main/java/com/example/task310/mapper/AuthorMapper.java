package com.example.task310.mapper;

import com.example.task310.domain.dto.request.AuthorRequestTo;
import com.example.task310.domain.dto.response.AuthorResponseTo;
import com.example.task310.domain.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorRequestTo request);

    AuthorResponseTo toResponse(Author author);
}