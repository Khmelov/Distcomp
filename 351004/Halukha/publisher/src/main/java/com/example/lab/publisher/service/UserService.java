package com.example.lab.publisher.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.lab.publisher.dto.UserRequestTo;
import com.example.lab.publisher.dto.UserResponseTo;
import com.example.lab.publisher.exception.EntityNotFoundException;
import com.example.lab.publisher.mapper.UserMapper;
import com.example.lab.publisher.model.User;
import com.example.lab.publisher.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper = UserMapper.INSTANCE;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(cacheNames = "users", key = "'all'")
    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "users", key = "#id")
    public UserResponseTo getUserById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "users", key = "'all'")
    })
    public UserResponseTo createUser(UserRequestTo request) {
        User user = mapper.toEntity(request);
        User saved = userRepository.save(user);
        return mapper.toDto(saved);
    }

    @Caching(put = {
            @CachePut(cacheNames = "users", key = "#id")
    }, evict = {
            @CacheEvict(cacheNames = "users", key = "'all'")
    })
    public UserResponseTo updateUser(Long id, UserRequestTo request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));
        User updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        User saved = userRepository.save(updated);
        return mapper.toDto(saved);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "users", key = "#id"),
            @CacheEvict(cacheNames = "users", key = "'all'")
    })
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found", 40401);
        }
        userRepository.deleteById(id);
    }
}
