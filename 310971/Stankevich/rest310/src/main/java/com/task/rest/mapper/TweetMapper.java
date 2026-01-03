package com.task.rest.mapper;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.model.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TweetMapper {
    Tweet toEntity(TweetRequestTo dto);
    TweetResponseTo toDto(Tweet entity);
    List<TweetResponseTo> toDtoList(List<Tweet> entities);
}