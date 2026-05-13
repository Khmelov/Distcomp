package com.example.distcomp.mapper

import com.example.distcomp.dto.request.CreatorRequestTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.model.Creator
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface CreatorMapper {
    @Mapping(target = "role", ignore = true)
    fun toEntity(request: CreatorRequestTo): Creator
    fun toResponse(entity: Creator): CreatorResponseTo
}
