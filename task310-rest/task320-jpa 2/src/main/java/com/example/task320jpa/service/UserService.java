package com.example.task320jpa.service;

import com.example.task320jpa.dto.request.UserRequestTo;
import com.example.task320jpa.dto.response.UserResponseTo;
import com.example.task320jpa.entity.User;
import com.example.task320jpa.exception.ResourceNotFoundException;
import com.example.task320jpa.mapper.UserMapper;
import com.example.task320jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с User
 * Поддерживает пагинацию и сортировку
 */
@Service
@RequiredArgsConstructor
@Transactional
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
    @Transactional(readOnly = true)
    public UserResponseTo getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponseTo(user);
    }
    
    /**
     * Получить всех пользователей с пагинацией
     */
    @Transactional(readOnly = true)
    public Page<UserResponseTo> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponseTo);
    }
    
    /**
     * Обновить пользователя
     */
    public UserResponseTo update(Long id, UserRequestTo requestTo) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        existingUser.setLogin(requestTo.getLogin());
        existingUser.setPassword(requestTo.getPassword());
        existingUser.setFirstname(requestTo.getFirstname());
        existingUser.setLastname(requestTo.getLastname());
        
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseTo(updatedUser);
    }
    
    /**
     * Частичное обновление пользователя (PATCH)
     */
    public UserResponseTo partialUpdate(Long id, UserRequestTo requestTo) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        userMapper.updateEntityFromRequestTo(requestTo, existingUser);
        User updatedUser = userRepository.save(existingUser);
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
