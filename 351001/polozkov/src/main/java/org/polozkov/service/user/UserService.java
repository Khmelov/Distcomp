package org.polozkov.service.user;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.user.UserRequestTo;
import org.polozkov.dto.user.UserResponseTo;
import org.polozkov.entity.issue.Issue;
import org.polozkov.entity.user.User;
import org.polozkov.exception.BadRequestException;
import org.polozkov.exception.ForbiddenException;
import org.polozkov.mapper.user.UserMapper;
import org.polozkov.repository.user.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    // Кешируем список всех пользователей.
    // При любом изменении (создание, удаление) этот кеш нужно сбрасывать.
    @Cacheable(value = "users_list")
    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToResponseDto)
                .toList();
    }

    // Кешируем конкретного пользователя по ID.
    @Transactional
    @Cacheable(value = "users", key = "#id")
    public UserResponseTo getUser(Long id) {
        User user = userRepository.byId(id);
        return userMapper.userToResponseDto(user);
    }

    @Transactional
    @CacheEvict(value = "users_list", allEntries = true)
    public UserResponseTo createUser(@Valid UserRequestTo userRequest) {
        if (userRepository.findByLogin(userRequest.getLogin()).isPresent()) {
            throw new ForbiddenException("User with login " + userRequest.getLogin() + " already exists");
        }

        User user = userMapper.requestDtoToUser(userRequest);
        user.setIssues(new ArrayList<>());

        User savedUser = userRepository.save(user);
        return userMapper.userToResponseDto(savedUser);
    }

    @Transactional
    // Обновляем запись в кеше "users" и сбрасываем общий список
    @CachePut(value = "users", key = "#userRequest.id")
    @CacheEvict(value = "users_list", allEntries = true)
    public UserResponseTo updateUser(@Valid UserRequestTo userRequest) {
        User existingUser = userRepository.byId(userRequest.getId());
        User user = userMapper.updateUser(existingUser, userRequest);
        user.setIssues(existingUser.getIssues());

        User updatedUser = userRepository.save(user);
        return userMapper.userToResponseDto(updatedUser);
    }

    @Transactional
    // Удаляем запись из обоих кешей
    @CacheEvict(value = {"users", "users_list"}, key = "#id", allEntries = false, beforeInvocation = false)
    public void deleteUser(Long id) {
        User user = userRepository.byId(id);

        if (!user.getIssues().isEmpty()) {
            throw new BadRequestException("Cannot delete user with existing issues. Delete issues first.");
        }

        userRepository.delete(user);
    }

    // Внутренний метод для получения сущности.
    // Если он используется часто, тоже можно кешировать (но осторожно с Hibernate-сущностями)
    @Transactional
    @Cacheable(value = "users_entities", key = "#id")
    public User getUserById(Long id) {
        return userRepository.byId(id);
    }

    @Transactional
    @CacheEvict(value = "users_entities", key = "#userId")
    public void addIssueToUser(Long userId, Issue issue) {
        User user = userRepository.byId(userId);
        user.getIssues().add(issue);
        userRepository.save(user);
    }
}