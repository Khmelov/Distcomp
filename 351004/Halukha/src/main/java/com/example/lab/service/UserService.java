package com.example.lab.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lab.dto.UserRequestTo;
import com.example.lab.dto.UserResponseTo;
import com.example.lab.exception.EntityNotFoundException;
import com.example.lab.mapper.UserMapper;
import com.example.lab.model.User;
import com.example.lab.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper = UserMapper.INSTANCE;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public UserResponseTo getUserById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));
    }

    public UserResponseTo createUser(UserRequestTo request) {
        User user = mapper.toEntity(request);
        User saved = userRepository.save(user);
        return mapper.toDto(saved);
    }

    public UserResponseTo updateUser(Long id, UserRequestTo request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));
        User updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        User saved = userRepository.save(updated);
        return mapper.toDto(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found", 40401);
        }
        userRepository.deleteById(id);
    }
}