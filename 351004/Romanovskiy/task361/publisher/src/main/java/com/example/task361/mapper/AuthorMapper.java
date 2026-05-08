package com.example.task361.mapper;

import com.example.task361.domain.dto.request.AuthorRequestTo;
import com.example.task361.domain.dto.response.AuthorResponseTo;
import com.example.task361.domain.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AuthorMapper {
    Author toEntity(AuthorRequestTo request);

    AuthorResponseTo toResponse(Author author);
}