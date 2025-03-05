package com.homel.user_stories.service;

import com.homel.user_stories.dto.UserRequestTo;
import com.homel.user_stories.dto.UserResponseTo;
import com.homel.user_stories.exception.EntityNotFoundException;
import com.homel.user_stories.mapper.UserMapper;
import com.homel.user_stories.model.User;
import com.homel.user_stories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseTo createUser(UserRequestTo userRequest) {
        User user = UserMapper.INSTANCE.toEntity(userRequest);
        User savedUser = userRepository.save(user);
        return UserMapper.INSTANCE.toResponse(savedUser);
    }

    public UserResponseTo getUser(Long id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper.INSTANCE::toResponse)
                .toList();
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        userRepository.deleteById(id);
    }

    public UserResponseTo updateUser(UserRequestTo userRequest) {
        User existingUser = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setLogin(userRequest.getLogin());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setFirstname(userRequest.getFirstname());
        existingUser.setLastname(userRequest.getLastname());

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.INSTANCE.toResponse(updatedUser);
    }
}
