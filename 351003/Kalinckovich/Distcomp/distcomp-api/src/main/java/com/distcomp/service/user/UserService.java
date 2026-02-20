package com.distcomp.service.user;

import com.distcomp.data.repository.user.UserReactiveRepository;
import com.distcomp.dto.user.UserCreateRequest;
import com.distcomp.dto.user.UserPatchRequest;
import com.distcomp.dto.user.UserResponseDto;
import com.distcomp.dto.user.UserUpdateRequest;
import com.distcomp.mapper.user.UserMapper;
import com.distcomp.model.user.User;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserReactiveRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Mono<UserResponseDto> create(final UserCreateRequest request) {
        return userValidator.validateCreate(request, ValidationArgs.empty())
                .flatMap(validationResult -> {
                    final User entity = userMapper.toEntity(request);
                    entity.setPassword(passwordEncoder.encode(entity.getPassword()));
                    return userRepository.save(entity);
                })
                .map(userMapper::toResponse);
    }

    public Flux<UserResponseDto> findAll(final int page, final int size) {
        final Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllBy(pageable)
                .map(userMapper::toResponse);
    }

    public Mono<UserResponseDto> findById(final Long id) {
        return userValidator.validateUserExists(id)
                .then(userRepository.findById(id))
                .map(userMapper::toResponse);
    }

    public Mono<UserResponseDto> findByLogin(final String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::toResponse);
    }

    public Mono<UserResponseDto> update(final Long id, final UserUpdateRequest request) {
        return userValidator.validateUpdate(request, ValidationArgs.withId(id))
                .flatMap(validationResult -> userRepository.findById(id))
                .flatMap(existing -> {
                    final User userToUpdate = userMapper.updateFromDto(request, existing);
                    if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        userToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    return userRepository.save(userToUpdate);
                })
                .map(userMapper::toResponse);
    }

    public Mono<UserResponseDto> patch(final Long id, final UserPatchRequest request) {
        return userValidator.validateUserExists(id)
                .then(userRepository.findById(id))
                .flatMap(existing -> {
                    final User userToUpdate = userMapper.updateFromPatch(request, existing);
                    if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        userToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    return userRepository.save(userToUpdate);
                })
                .map(userMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return userValidator.validateUserExists(id)
                .then(userRepository.deleteById(id));
    }
}