package com.example.lab1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab1.dto.UserRequestTo;
import com.example.lab1.dto.UserResponseTo;
import com.example.lab1.model.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestTo dto);

    UserResponseTo toDto(User entity);

    @Mappings({
            @Mapping(target = "login", source = "dto.login"),
            @Mapping(target = "password", source = "dto.password"),
            @Mapping(target = "firstName", source = "dto.firstName"),
            @Mapping(target = "lastName", source = "dto.lastName"),
            @Mapping(target = "id", ignore = true)
    })
    User updateEntity(UserRequestTo dto, User existing);
}
