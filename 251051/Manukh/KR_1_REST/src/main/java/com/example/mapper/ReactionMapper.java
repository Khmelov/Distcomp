// ReactionMapper.java
package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.dto.request.ReactionRequestTo;
import com.example.dto.response.ReactionResponseTo;
import com.example.model.Reaction;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Reaction toEntity(ReactionRequestTo request);

    ReactionResponseTo toResponse(Reaction reaction);

    List<ReactionResponseTo> toResponseList(List<Reaction> reactions);
}