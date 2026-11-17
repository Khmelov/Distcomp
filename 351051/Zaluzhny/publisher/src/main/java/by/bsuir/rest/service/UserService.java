package by.bsuir.rest.service;

import by.bsuir.rest.dto.request.UserRequestTo;
import by.bsuir.rest.dto.response.UserResponseTo;

import java.util.List;

public interface UserService {

    UserResponseTo create(UserRequestTo request);

    UserResponseTo getById(Long id);

    List<UserResponseTo> getAll();

    UserResponseTo update(Long pathId, UserRequestTo request);

    void delete(Long id);
}
