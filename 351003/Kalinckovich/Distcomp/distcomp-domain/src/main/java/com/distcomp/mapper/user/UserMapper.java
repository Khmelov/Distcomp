package com.distcomp.mapper.user;

import com.distcomp.dto.user.UserCreateRequest;
import com.distcomp.dto.user.UserPatchRequest;
import com.distcomp.dto.user.UserResponseDto;
import com.distcomp.dto.user.UserUpdateRequest;
import com.distcomp.mapper.config.MappedConfig;
import com.distcomp.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappedConfig.class)
public interface UserMapper {

    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "firstname", target = "firstname")
    UserResponseDto toResponse(User entity);

    User toEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User updateFromDto(UserUpdateRequest dto, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User updateFromPatch(UserPatchRequest dto, @MappingTarget User entity);
}
