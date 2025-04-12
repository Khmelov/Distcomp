package com.lab1.lab1DC.mapper;

import com.lab1.lab1DC.dto.UserRequestTo;
import com.lab1.lab1DC.dto.UserResponseTo;
import com.lab1.lab1DC.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserRequestTo userRequestTo);

    UserResponseTo toResponseDto(User user);

    User toEntity(UserResponseTo userResponseTo);

    UserRequestTo toRequestDto(User user);
}
