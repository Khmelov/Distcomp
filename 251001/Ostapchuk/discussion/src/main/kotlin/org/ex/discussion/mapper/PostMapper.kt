package org.ex.discussion.mapper

import org.ex.discussion.dto.request.PostRequestDTO
import org.ex.discussion.dto.response.PostResponseDTO
import org.ex.discussion.model.Post
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants.ComponentModel

@Mapper(
    componentModel = ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface PostMapper {

    fun toDto(entity: Post): PostResponseDTO

    fun toEntity(dto: PostRequestDTO): Post
}