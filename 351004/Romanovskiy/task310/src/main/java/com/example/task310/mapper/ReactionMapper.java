package com.example.task310.mapper;

import com.example.task310.domain.dto.request.ReactionRequestTo;
import com.example.task310.domain.dto.response.ReactionResponseTo;
import com.example.task310.domain.entity.Reaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    Reaction toEntity(ReactionRequestTo request);

    ReactionResponseTo toResponse(Reaction reaction);
}