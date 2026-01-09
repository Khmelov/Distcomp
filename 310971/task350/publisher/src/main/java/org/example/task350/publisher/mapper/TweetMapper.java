package org.example.task350.publisher.mapper;

import org.example.task350.publisher.dto.TweetRequestTo;
import org.example.task350.publisher.dto.TweetResponseTo;
import org.example.task350.publisher.model.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TweetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "tweetLabels", ignore = true)
    Tweet toEntity(TweetRequestTo request);

    @Mapping(target = "writerId", expression = "java(entity.getWriter() != null ? entity.getWriter().getId() : null)")
    @Mapping(target = "labelIds", expression = "java(entity.getTweetLabels() != null ? entity.getTweetLabels().stream().map(tl -> tl.getLabel().getId()).collect(java.util.stream.Collectors.toSet()) : new java.util.HashSet<>())")
    TweetResponseTo toDto(Tweet entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "tweetLabels", ignore = true)
    void updateEntityFromDto(TweetRequestTo request, @MappingTarget Tweet entity);
}

