package com.blog.mapper;

import com.blog.dto.ReactionRequestTo;
import com.blog.dto.ReactionResponseTo;
import com.blog.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    Reaction requestToToEntity(ReactionRequestTo request);

    ReactionResponseTo entityToResponseTo(Reaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Reaction updateEntityFromRequest(ReactionRequestTo request, @MappingTarget Reaction entity);
}