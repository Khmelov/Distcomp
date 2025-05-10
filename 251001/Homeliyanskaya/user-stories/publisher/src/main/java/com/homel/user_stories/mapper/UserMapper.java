package com.homel.user_stories.mapper;

import com.homel.user_stories.dto.UserRequestTo;
import com.homel.user_stories.dto.UserResponseTo;
import com.homel.user_stories.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserRequestTo dto);
    UserResponseTo toResponse(User entity);
}
