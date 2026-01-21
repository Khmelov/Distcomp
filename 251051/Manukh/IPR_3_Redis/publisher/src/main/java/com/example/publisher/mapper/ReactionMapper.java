package com.example.publisher.mapper;

import com.example.publisher.dto.request.ReactionRequestTo;
import com.example.publisher.dto.response.ReactionResponseTo;
import com.example.publisher.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "modifiedAt", target = "modifiedAt")
    @Mapping(source = "story.id", target = "storyId")
    ReactionResponseTo toResponse(Reaction reaction);

    @Mapping(source = "storyId", target = "story.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    Reaction toEntity(ReactionRequestTo request);

    List<ReactionResponseTo> toResponseList(List<Reaction> reactions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "story", ignore = true)
    void updateEntity(ReactionRequestTo request, @MappingTarget Reaction reaction);
}