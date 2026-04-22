package com.example.task350.mapper;

import com.example.task350.domain.dto.request.ReactionRequestTo;
import com.example.task350.domain.dto.response.ReactionResponseTo;
import com.example.task350.domain.entity.Reaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ReactionMapper {
    Reaction toEntity(ReactionRequestTo request);

    ReactionResponseTo toResponse(Reaction reaction);
}