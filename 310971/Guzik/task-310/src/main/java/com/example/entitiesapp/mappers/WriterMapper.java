package com.example.entitiesapp.mappers;

import com.example.entitiesapp.dto.request.WriterRequestTo;
import com.example.entitiesapp.dto.response.WriterResponseTo;
import com.example.entitiesapp.entities.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WriterMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(source = "firstname", target = "firstName")
    @Mapping(source = "lastname", target = "lastName")
    Writer toEntity(WriterRequestTo dto);

    @Mapping(source = "firstName", target = "firstname")
    @Mapping(source = "lastName", target = "lastname")
    @Mapping(source = "login", target = "login") // Убираем маппинг на "writer"
    WriterResponseTo toResponseDto(Writer entity);
}