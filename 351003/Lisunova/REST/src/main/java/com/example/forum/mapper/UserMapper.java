package com.example.forum.mapper;

import com.example.forum.dto.request.UserRequestTo;
import com.example.forum.dto.response.UserResponseTo;
import com.example.forum.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestTo dto);

    UserResponseTo toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserRequestTo dto, @MappingTarget User entity);
}
