package org.polozkov.service.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.user.UserRequestTo;
import org.polozkov.dto.user.UserResponseTo;
import org.polozkov.entity.issue.Issue;
import org.polozkov.entity.user.User;
import org.polozkov.exception.BadRequestException;
import org.polozkov.mapper.user.UserMapper;
import org.polozkov.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToResponseDto)
                .toList();
    }

    public UserResponseTo getUser(Long id) {
        User user = userRepository.getById(id);
        return userMapper.userToResponseDto(user);
    }

    public UserResponseTo createUser(@Valid UserRequestTo userRequest) {
        if (userRepository.findByLogin(userRequest.getLogin()).isPresent()) {
            throw new BadRequestException("User with login " + userRequest.getLogin() + " already exists");
        }

        User user = userMapper.requestDtoToUser(userRequest);

        user.setIssues(new ArrayList<>());

        User savedUser = userRepository.save(user);
        return userMapper.userToResponseDto(savedUser);
    }

    public UserResponseTo updateUser(@Valid UserRequestTo userRequest) {
        User existingUser = userRepository.getById(userRequest.getId());

        User user = userMapper.updateUser(existingUser, userRequest);

        user.setIssues(existingUser.getIssues());

        User updatedUser = userRepository.update(user);
        return userMapper.userToResponseDto(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.getById(id);

        if (!user.getIssues().isEmpty()) {
            throw new BadRequestException("Cannot delete user with existing issues. Delete issues first.");
        }

        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        return userRepository.getById(id);
    }

    public void addIssueToUser(Long userId, Issue issue) {
        User user = userRepository.getById(userId);
        user.getIssues().add(issue);
        userRepository.update(user);
    }
}