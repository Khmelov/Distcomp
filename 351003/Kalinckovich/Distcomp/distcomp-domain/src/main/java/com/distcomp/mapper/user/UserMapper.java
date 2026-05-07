package com.distcomp.mapper.user;

import com.distcomp.dto.user.UserCreateRequest;
import com.distcomp.dto.user.UserPatchRequest;
import com.distcomp.dto.user.UserResponseDto;
import com.distcomp.dto.user.UserUpdateRequest;
import com.distcomp.mapper.config.MappedConfig;
import com.distcomp.model.user.User;
import com.distcomp.model.user.UserRole;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MappedConfig.class)
public interface UserMapper {


    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "firstname", target = "firstname")
    UserResponseDto toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = ".", qualifiedByName = "mapRole")
    User toEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User updateFromDto(UserUpdateRequest dto, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User updateFromPatch(UserPatchRequest dto, @MappingTarget User entity);

    @Named("mapRole")
    default UserRole mapRole(final UserCreateRequest request) {
        return request.getRole() != null ? request.getRole() : UserRole.CUSTOMER;
    }
}
