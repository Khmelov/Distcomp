package com.example.restApi.mapper;

import com.example.restApi.dto.request.UserRequestTo;
import com.example.restApi.dto.response.UserResponseTo;
import com.example.restApi.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestTo request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        return user;
    }

    public UserResponseTo toDto(User user) {
        UserResponseTo response = new UserResponseTo();
        response.setId(user.getId());
        response.setLogin(user.getLogin());
        response.setFirstname(user.getFirstname());
        response.setLastname(user.getLastname());
        return response;
    }

    public void updateEntity(User user, UserRequestTo request) {
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
    }
}
