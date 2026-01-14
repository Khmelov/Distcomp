package com.socialnetwork.mapper;

import com.socialnetwork.dto.request.UserRequestTo;
import com.socialnetwork.dto.response.UserResponseTo;
import com.socialnetwork.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestTo request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        return user;
    }

    public UserResponseTo toResponse(User entity) {
        if (entity == null) {
            return null;
        }

        UserResponseTo response = new UserResponseTo();
        response.setId(entity.getId());
        response.setLogin(entity.getLogin());
        response.setFirstname(entity.getFirstname());
        response.setLastname(entity.getLastname());
        return response;
    }
}