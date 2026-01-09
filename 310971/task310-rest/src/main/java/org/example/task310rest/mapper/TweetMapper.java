package org.example.task310rest.mapper;

import org.example.task310rest.dto.TweetRequestTo;
import org.example.task310rest.dto.TweetResponseTo;
import org.example.task310rest.model.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TweetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "labelIds", expression = "java(request.getLabelIds() == null ? new java.util.HashSet<>() : new java.util.HashSet<>(request.getLabelIds()))")
    Tweet toEntity(TweetRequestTo request);

    TweetResponseTo toDto(Tweet entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "labelIds", expression = "java(request.getLabelIds() == null ? entity.getLabelIds() : new java.util.HashSet<>(request.getLabelIds()))")
    void updateEntityFromDto(TweetRequestTo request, @MappingTarget Tweet entity);
}
