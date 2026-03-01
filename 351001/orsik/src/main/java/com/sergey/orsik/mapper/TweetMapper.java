package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.entity.Tweet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TweetMapper {

    Tweet toEntity(TweetRequestTo request);

    TweetResponseTo toResponse(Tweet entity);
}
