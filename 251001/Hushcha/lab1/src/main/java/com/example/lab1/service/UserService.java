package com.example.lab1.service;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.dto.UserRequestTo;
import com.example.lab1.dto.UserResponseTo;
import com.example.lab1.entity.User;
import com.example.lab1.mapper.UserMapper;
import com.example.lab1.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements EntityService<UserRequestTo, UserResponseTo> {

    private final EntityRepository<User> userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public UserResponseTo create(UserRequestTo entityDTO) {
        User user = userMapper.toEntity(entityDTO);
        User savedUser = userRepository.create(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserResponseTo getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public List<UserResponseTo> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseTo update(UserRequestTo entityDTO) {
        User user = userMapper.toEntity(entityDTO);
        User updatedUser = userRepository.update(user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}

