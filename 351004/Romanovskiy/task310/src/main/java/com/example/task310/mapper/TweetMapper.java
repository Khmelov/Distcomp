package com.example.task310.mapper;

import com.example.task310.domain.dto.request.TweetRequestTo;
import com.example.task310.domain.dto.response.TweetResponseTo;
import com.example.task310.domain.entity.Tweet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TweetMapper {
    Tweet toEntity(TweetRequestTo request);

    TweetResponseTo toResponse(Tweet tweet);
}