package com.distcomp.mapper

import com.distcomp.dto.user.UserRequestTo
import com.distcomp.dto.user.UserResponseTo
import com.distcomp.entity.User
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface UserMapper {
    fun toUserResponse(user: User) : UserResponseTo

    fun toUserEntity(userRequestTo: UserRequestTo) : User
}