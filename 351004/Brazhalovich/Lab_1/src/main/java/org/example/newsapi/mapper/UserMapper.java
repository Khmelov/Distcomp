package org.example.newsapi.mapper;

import org.example.newsapi.dto.request.UserRequestTo;
import org.example.newsapi.dto.response.UserResponseTo;
import org.example.newsapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true) // Игнорируем список новостей при создании
    User toEntity(UserRequestTo request);

    UserResponseTo toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    void updateEntityFromDto(UserRequestTo request, @MappingTarget User user);
}