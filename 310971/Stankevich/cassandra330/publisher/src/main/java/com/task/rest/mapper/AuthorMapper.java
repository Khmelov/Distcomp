package com.task.rest.mapper;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    AuthorResponseTo toResponseTo(Author author);

    Author toEntity(AuthorRequestTo requestTo);
}