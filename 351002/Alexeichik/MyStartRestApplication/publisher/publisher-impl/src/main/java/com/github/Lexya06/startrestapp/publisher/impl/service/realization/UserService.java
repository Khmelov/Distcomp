package com.github.Lexya06.startrestapp.publisher.impl.service.realization;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization.UserRepository;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.realization.UserMapper;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseEntityService<User, UserRequestTo, UserResponseTo> {
    @Getter
    private final UserRepository userRepository;

    @Getter
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository,  UserMapper userMapper) {
        super(User.class);
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    protected MyCrudRepositoryImpl<User> getRepository() {
        return userRepository;
    }

    @Override
    protected GenericMapperImpl<User, UserRequestTo, UserResponseTo> getMapper() {
        return userMapper;
    }
}
