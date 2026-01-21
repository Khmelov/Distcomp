package com.example.publisher.mapper;

import com.example.publisher.dto.request.ReactionRequestTo;
import com.example.publisher.dto.response.ReactionResponseTo;
import com.example.publisher.entity.Reaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "story", ignore = true)
    Reaction toEntity(ReactionRequestTo request);

    @Mapping(source = "story.id", target = "storyId")
    @Mapping(source = "createdAt", target = "created")
    @Mapping(source = "modifiedAt", target = "modified")
    ReactionResponseTo toResponse(Reaction reaction);

    List<ReactionResponseTo> toResponseList(List<Reaction> reactions);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "story", ignore = true)
    void updateEntity(ReactionRequestTo request, @MappingTarget Reaction reaction);
}