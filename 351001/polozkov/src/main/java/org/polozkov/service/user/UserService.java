package org.polozkov.service.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.user.UserRequestTo;
import org.polozkov.dto.user.UserResponseTo;
import org.polozkov.entity.user.User;
import org.polozkov.exception.NotFoundException;
import org.polozkov.mapper.user.UserMapper;
import org.polozkov.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseTo getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::userToResponseDto)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserResponseTo createUser(@Valid UserRequestTo userRequest) {
        userRepository.findByLogin(userRequest.getLogin())
                .ifPresent(user -> {
                    throw new RuntimeException("User with login " + userRequest.getLogin() + " already exists");
                });

        User user = userMapper.requestDtoToUser(userRequest);
        User savedUser = userRepository.save(user);
        return userMapper.userToResponseDto(savedUser);
    }

    public UserResponseTo updateUser(@Valid UserRequestTo userRequest) {
        if (!userRepository.existsById(userRequest.getId())) {
            throw new RuntimeException("User not found with id: " + userRequest.getId());
        }

        User user = userRepository.findById(userRequest.getId()).orElseThrow(() -> new NotFoundException("User not found with id: " + userRequest.getId()));
        user = userMapper.updateUser(user, userRequest);
        return userMapper.userToResponseDto(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}