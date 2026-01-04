package com.task310.socialnetwork.service;

import com.task310.socialnetwork.dto.request.UserRequestTo;
import com.task310.socialnetwork.dto.response.UserResponseTo;
import java.util.List;

public interface UserService {
    List<UserResponseTo> getAll();
    UserResponseTo getById(Long id);
    UserResponseTo create(UserRequestTo request);
    UserResponseTo update(Long id, UserRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
}