package com.task310.blogplatform.mapper;

import com.task310.blogplatform.dto.UserRequestTo;
import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "articles", ignore = true)
    User toEntity(UserRequestTo dto);

    UserResponseTo toResponseDto(User entity);

    List<UserResponseTo> toResponseDtoList(List<User> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "articles", ignore = true)
    void updateEntityFromDto(UserRequestTo dto, @MappingTarget User entity);
}

