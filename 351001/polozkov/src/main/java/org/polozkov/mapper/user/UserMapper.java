package org.polozkov.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.polozkov.dto.user.UserRequestTo;
import org.polozkov.dto.user.UserResponseTo;
import org.polozkov.entity.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseTo userToResponseDto(User user);

    User requestDtoToUser(UserRequestTo userRequest);

    @Mapping(target = "id", ignore = true)
    User updateUser(@MappingTarget User target, UserRequestTo userRequest);
}
