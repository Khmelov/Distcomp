package com.example.discussion.service;

import com.example.discussion.dto.UserRequestTo;
import com.example.discussion.dto.UserResponseTo;
import com.example.discussion.exception.DuplicateLoginException;
import com.example.discussion.model.User;
import com.example.discussion.repository.UserRepository;
import com.example.discussion.service.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseTo> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseTo findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper::toDto).orElse(null);
    }

    @Transactional
    public UserResponseTo save(UserRequestTo userRequestTo) {
        if (userRepository.findByLogin(userRequestTo.getLogin()).isPresent()) {
            throw new DuplicateLoginException("Login already exists");
        }
        User user = userMapper.toEntity(userRequestTo);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserResponseTo update(UserRequestTo userRequestTo) {
        User existingUser = userRepository.findById(userRequestTo.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateEntityFromDto(userRequestTo, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}