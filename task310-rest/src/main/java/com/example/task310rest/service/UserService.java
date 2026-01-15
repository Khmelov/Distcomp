package com.example.task310rest.service;

import com.example.task310rest.dto.request.UserRequestTo;
import com.example.task310rest.dto.response.UserResponseTo;
import com.example.task310rest.entity.User;
import com.example.task310rest.exception.ResourceNotFoundException;
import com.example.task310rest.mapper.UserMapper;
import com.example.task310rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с User
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    /**
     * Создать нового пользователя
     */
    public UserResponseTo create(UserRequestTo requestTo) {
        User user = userMapper.toEntity(requestTo);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseTo(savedUser);
    }
    
    /**
     * Получить пользователя по ID
     */
    public UserResponseTo getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponseTo(user);
    }
    
    /**
     * Получить всех пользователей
     */
    public List<UserResponseTo> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseTo)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновить пользователя
     */
    public UserResponseTo update(Long id, UserRequestTo requestTo) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        // Обновляем поля
        existingUser.setLogin(requestTo.getLogin());
        existingUser.setPassword(requestTo.getPassword());
        existingUser.setFirstname(requestTo.getFirstname());
        existingUser.setLastname(requestTo.getLastname());
        
        User updatedUser = userRepository.update(existingUser);
        return userMapper.toResponseTo(updatedUser);
    }
    
    /**
     * Частичное обновление пользователя (PATCH)
     */
    public UserResponseTo partialUpdate(Long id, UserRequestTo requestTo) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        userMapper.updateEntityFromRequestTo(requestTo, existingUser);
        User updatedUser = userRepository.update(existingUser);
        return userMapper.toResponseTo(updatedUser);
    }
    
    /**
     * Удалить пользователя
     */
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}
