package com.example.restApi.services;

import com.example.restApi.dto.request.UserRequestTo;
import com.example.restApi.dto.response.UserResponseTo;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.model.User;
import com.example.restApi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponseTo> getAll(int page, int size, String sortParam) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam));
        return userRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }

    public UserResponseTo getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return convertToResponseDto(user);
    }

    @Transactional
    public UserResponseTo create(UserRequestTo request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());

        User saved = userRepository.save(user);
        return convertToResponseDto(saved);
    }

    @Transactional
    public UserResponseTo update(Long id, UserRequestTo request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setModified(LocalDateTime.now());

        User updated = userRepository.save(user);
        return convertToResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseTo convertToResponseDto(User user) {
        UserResponseTo dto = new UserResponseTo();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setCreated(user.getCreated());
        dto.setModified(user.getModified());
        return dto;
    }
}