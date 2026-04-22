package org.example.newsapi.mapper;

import org.example.newsapi.dto.request.MarkerRequestTo;
import org.example.newsapi.dto.request.UserRequestTo;
import org.example.newsapi.dto.response.MarkerResponseTo;
import org.example.newsapi.dto.response.UserResponseTo;
import org.example.newsapi.entity.Marker;
import org.example.newsapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo request);
    MarkerResponseTo toDto(Marker marker);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MarkerRequestTo request, @MappingTarget Marker marker);
}