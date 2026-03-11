package com.github.Lexya06.startrestapp.service.mapper.realization;


import com.github.Lexya06.startrestapp.model.dto.request.UserRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.UserResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.User;
import com.github.Lexya06.startrestapp.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface UserMapper extends GenericMapperImpl<User, UserRequestTo, UserResponseTo> {

}
