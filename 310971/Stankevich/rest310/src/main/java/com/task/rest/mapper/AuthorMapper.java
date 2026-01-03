package com.task.rest.mapper;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {
    Author toEntity(AuthorRequestTo dto);
    AuthorResponseTo toDto(Author entity);
    List<AuthorResponseTo> toDtoList(List<Author> entities);
}