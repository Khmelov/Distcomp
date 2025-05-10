package com.example.restservice.mapper;

import com.example.restservice.dto.AuthorCreateDto;
import com.example.restservice.dto.AuthorResponseDto;
import com.example.restservice.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorCreateDto dto);
    AuthorResponseDto toDto(Author entity);
}
