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
    private final RedisCacheService cacheService;

    private static final String USER_CACHE_PREFIX = "user::";

    @Autowired
    public UserService(UserRepository userRepository, RedisCacheService cacheService) {
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    public UserResponseTo createUser(UserRequestTo userRequest) {
        User user = UserMapper.INSTANCE.toEntity(userRequest);
        User savedUser = userRepository.save(user);
        
        cacheService.put(USER_CACHE_PREFIX + savedUser.getId(), UserMapper.INSTANCE.toResponse(savedUser));

        return UserMapper.INSTANCE.toResponse(savedUser);
    }

    public UserResponseTo getUser(Long id) {
        String cacheKey = USER_CACHE_PREFIX + id;
        UserResponseTo cachedUser = cacheService.get(cacheKey, UserResponseTo.class);

        if (cachedUser != null) {
            return cachedUser;
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserResponseTo userResponse = UserMapper.INSTANCE.toResponse(user);

        cacheService.put(cacheKey, userResponse);

        return userResponse;
    }

    public List<UserResponseTo> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseTo> usersResponse = users.stream()
                .map(UserMapper.INSTANCE::toResponse)
                .toList();


        return usersResponse;
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        userRepository.deleteById(id);

        cacheService.evict(USER_CACHE_PREFIX + id);
    }

    public UserResponseTo updateUser(UserRequestTo userRequest) {
        User existingUser = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setLogin(userRequest.getLogin());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setFirstname(userRequest.getFirstname());
        existingUser.setLastname(userRequest.getLastname());

        User updatedUser = userRepository.save(existingUser);

        cacheService.put(USER_CACHE_PREFIX + updatedUser.getId(), UserMapper.INSTANCE.toResponse(updatedUser));

        return UserMapper.INSTANCE.toResponse(updatedUser);
    }
}
