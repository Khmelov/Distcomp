package com.example.discussion.mapper;

import com.example.discussion.dto.request.AuthorRequestTo;
import com.example.discussion.dto.response.AuthorResponseTo;
import com.example.discussion.model.Author;
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