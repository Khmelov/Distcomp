package com.example.task320.mapper;

import com.example.task320.domain.dto.request.AuthorRequestTo;
import com.example.task320.domain.dto.response.AuthorResponseTo;
import com.example.task320.domain.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AuthorMapper {
    Author toEntity(AuthorRequestTo request);

    AuthorResponseTo toResponse(Author author);
}