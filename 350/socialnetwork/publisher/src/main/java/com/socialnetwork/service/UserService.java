package com.socialnetwork.service;

import com.socialnetwork.dto.request.UserRequestTo;
import com.socialnetwork.dto.response.UserResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    List<UserResponseTo> getAll();
    Page<UserResponseTo> getAll(Pageable pageable);
    UserResponseTo getById(Long id);
    UserResponseTo create(UserRequestTo request);
    UserResponseTo update(Long id, UserRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
}