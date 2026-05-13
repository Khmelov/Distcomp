package com.example.forum.service;

import com.example.forum.dto.request.UserRequestTo;
import com.example.forum.dto.response.UserResponseTo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    UserResponseTo create(UserRequestTo request);

    UserResponseTo getById(Long id);

    Page<UserResponseTo> getAll(String loginFilter, org.springframework.data.domain.Pageable pageable);

    UserResponseTo update(Long id, UserRequestTo request);

    void delete(Long id);
}
