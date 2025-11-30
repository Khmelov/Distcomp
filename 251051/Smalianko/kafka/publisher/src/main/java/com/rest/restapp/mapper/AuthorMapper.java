package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.AuthorRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import com.rest.restapp.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    Author toEntity(AuthorRequestToDto requestTo);

    AuthorResponseToDto toResponseTo(Author entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(AuthorRequestToDto requestTo, @MappingTarget Author entity);
}