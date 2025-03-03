package com.example.lab1.mapper;

import com.example.lab1.dto.ReactionRequestTo;
import com.example.lab1.dto.ReactionResponseTo;
import com.example.lab1.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReactionMapper {
    ReactionMapper INSTANCE = Mappers.getMapper(ReactionMapper.class);

    ReactionResponseTo toDTO(Reaction reaction);
    Reaction toEntity(ReactionRequestTo reactionRequestTo);
}
