package com.example.rest.mapper;
import com.example.rest.dto.requestDto.UserRequestTo;
import com.example.rest.dto.responseDto.UserResponseTo;
import com.example.rest.dto.updateDto.UserUpdateTo;
import com.example.rest.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseTo ToUserResponseTo(User user);
    User ToUser(UserRequestTo userRequestTo);
    User ToUser(UserUpdateTo userUpdateTo);
}
