package com.example.restapp.mapper;

import com.example.restapp.dto.request.AuthorRequestTo;
import com.example.restapp.dto.response.AuthorResponseTo;
import com.example.restapp.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorRequestTo request);
    AuthorResponseTo toResponse(Author entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(AuthorRequestTo request, @MappingTarget Author entity);
}