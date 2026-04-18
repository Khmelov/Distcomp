package com.example.forum.service;

import com.example.forum.dto.request.UserRequestTo;
import com.example.forum.dto.response.UserResponseTo;

import java.util.List;

public interface UserService {

    UserResponseTo create(UserRequestTo request);

    UserResponseTo getById(Long id);

    List<UserResponseTo> getAll();

    UserResponseTo update(Long id, UserRequestTo request);

    void delete(Long id);
}
