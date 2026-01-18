package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.UserRequest;
import com.rest.restapp.dto.response.UserResponseTo;
import com.rest.restapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserRequest requestTo);

    UserResponseTo toResponseTo(User entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserRequest requestTo, @MappingTarget User entity);
}