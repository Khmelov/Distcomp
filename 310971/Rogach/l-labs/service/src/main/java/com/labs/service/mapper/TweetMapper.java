package com.labs.service.mapper;

import com.labs.domain.entity.Tweet;
import com.labs.service.dto.TweetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TweetMapper {
    @Mapping(source = "writer.id", target = "writerId")
    TweetDto toDto(Tweet tweet);

    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Tweet toEntity(TweetDto tweetDto);
}

