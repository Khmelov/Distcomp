package com.socialnetwork.service.impl;

import com.socialnetwork.dto.request.UserRequestTo;
import com.socialnetwork.dto.response.UserResponseTo;
import com.socialnetwork.exception.DuplicateResourceException;
import com.socialnetwork.exception.ResourceNotFoundException;
import com.socialnetwork.mapper.UserMapper;
import com.socialnetwork.model.User;
import com.socialnetwork.repository.UserRepository;
import com.socialnetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserResponseTo> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponseTo> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponseTo getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseTo create(UserRequestTo request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new DuplicateResourceException("User with login '" + request.getLogin() + "' already exists");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponseTo update(Long id, UserRequestTo request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getLogin().equals(request.getLogin()) &&
                userRepository.existsByLogin(request.getLogin())) {
            throw new IllegalArgumentException("User with login '" + request.getLogin() + "' already exists");
        }

        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}