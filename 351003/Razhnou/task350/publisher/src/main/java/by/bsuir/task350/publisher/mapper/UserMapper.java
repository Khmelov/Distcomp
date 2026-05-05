package by.bsuir.task350.publisher.mapper;

import by.bsuir.task350.publisher.dto.request.UserRequestTo;
import by.bsuir.task350.publisher.dto.response.UserResponseTo;
import by.bsuir.task350.publisher.entity.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static User toEntity(UserRequestTo request) {
        User user = new User();
        updateEntity(user, request);
        return user;
    }

    public static void updateEntity(User user, UserRequestTo request) {
        user.setLogin(request.login().trim());
        user.setPassword(request.password().trim());
        user.setFirstname(request.firstname().trim());
        user.setLastname(request.lastname().trim());
    }

    public static UserResponseTo toResponse(User user) {
        return new UserResponseTo(user.getId(), user.getLogin(), user.getPassword(), user.getFirstname(), user.getLastname());
    }
}
