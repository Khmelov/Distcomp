package com.github.Lexya06.startrestapp.publisher.impl.service.realization;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization.UserRepository;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.enums.UserRole;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.realization.UserMapper;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "users")
public class UserService extends BaseEntityService<User, UserRequestTo, UserResponseTo> {
    @Getter
    private final UserRepository userRepository;

    @Getter
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        super(User.class);
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected MyCrudRepositoryImpl<User> getRepository() {
        return userRepository;
    }

    @Override
    protected GenericMapperImpl<User, UserRequestTo, UserResponseTo> getMapper() {
        return userMapper;
    }

    @Override
    @Cacheable(key = "#id")
    public UserResponseTo getEntityById(Long id) {
        return super.getEntityById(id);
    }

    @Override
    protected void preCreate(User user, UserRequestTo requestDTO) {
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        if (requestDTO.getRole() != null) {
            user.setRole(UserRole.valueOf(requestDTO.getRole()));
        } else {
            user.setRole(UserRole.CUSTOMER);
        }
    }

    @Override
    protected void preUpdate(User user, UserRequestTo requestDTO) {
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }
        if (requestDTO.getRole() != null) {
            user.setRole(UserRole.valueOf(requestDTO.getRole()));
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteEntityById(Long id) {
        super.deleteEntityById(id);
    }
}
