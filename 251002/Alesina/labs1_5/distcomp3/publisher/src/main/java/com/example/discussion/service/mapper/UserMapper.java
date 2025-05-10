package com.example.discussion.service.mapper;

import com.example.discussion.dto.UserRequestTo;
import com.example.discussion.dto.UserResponseTo;
import com.example.discussion.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(target = "id", ignore = true)  // Игнорируем ID при создании
    User toEntity(UserRequestTo dto);

    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    UserResponseTo toDto(User user);

    @Mapping(target = "id", ignore = true)  // Игнорируем ID при обновлении
    void updateEntityFromDto(UserRequestTo dto, @MappingTarget User entity);
}