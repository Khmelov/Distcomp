package org.example.newsapi.service;


import org.example.newsapi.dto.request.UserRequestTo;
import org.example.newsapi.dto.response.UserResponseTo;
import org.example.newsapi.entity.User;
import org.example.newsapi.exception.NotFoundException;
import org.example.newsapi.mapper.UserMapper;
import org.example.newsapi.repository.impl.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseTo create(UserRequestTo request) {
        User user = userMapper.toEntity(request);
        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserResponseTo> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserResponseTo findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public UserResponseTo update(Long id, UserRequestTo request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        userMapper.updateEntityFromDto(request, user);
        return userMapper.toDto(userRepository.update(user));
    }

    public void delete(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}