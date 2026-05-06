package com.github.Lexya06.startrestapp.publisher.impl.service.mapper.realization;


import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface UserMapper extends GenericMapperImpl<User, UserRequestTo, UserResponseTo> {

}
