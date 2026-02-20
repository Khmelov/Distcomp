package com.github.Lexya06.startrestapp.service.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.User;
import com.github.Lexya06.startrestapp.model.dto.request.UserRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.UserResponseTo;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.service.mapper.realization.UserMapper;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseEntityService<User, UserRequestTo, UserResponseTo> {
    MyCrudRepository<User> userRepository;
    UserMapper userMapper;

    @Autowired
    public UserService(MyCrudRepository<User> userRepository,  UserMapper userMapper) {
        super(User.class);
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    protected MyCrudRepository<User> getRepository() {
        return userRepository;
    }

    @Override
    protected GenericMapperImpl<User, UserRequestTo, UserResponseTo> getMapper() {
        return userMapper;
    }
}
