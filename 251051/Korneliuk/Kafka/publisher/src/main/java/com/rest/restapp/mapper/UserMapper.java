package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.UserRequestTo;
import com.rest.restapp.dto.response.UserResponseTo;
import com.rest.restapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserRequestTo requestTo);

    UserResponseTo toResponseTo(User entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserRequestTo requestTo, @MappingTarget User entity);
}