package com.example.lab.publisher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab.publisher.dto.UserRequestTo;
import com.example.lab.publisher.dto.UserResponseTo;
import com.example.lab.publisher.model.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestTo dto);

    UserResponseTo toDto(User entity);

    @Mappings({
            @Mapping(target = "login", source = "dto.login"),
            @Mapping(target = "password", source = "dto.password"),
            @Mapping(target = "firstname", source = "dto.firstname"),
            @Mapping(target = "lastname", source = "dto.lastname"),
            @Mapping(target = "id", ignore = true)
    })
    User updateEntity(UserRequestTo dto, User existing);
}
