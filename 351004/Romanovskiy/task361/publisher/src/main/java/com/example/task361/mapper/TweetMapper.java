package com.example.task361.mapper;

import com.example.task361.domain.dto.request.TweetRequestTo;
import com.example.task361.domain.dto.response.TweetResponseTo;
import com.example.task361.domain.entity.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping; // ДОБАВИТЬ ЭТО
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TweetMapper {
    
    @Mapping(target = "markers", ignore = true) // ДОБАВИТЬ ЭТУ СТРОКУ
    Tweet toEntity(TweetRequestTo request);

    TweetResponseTo toResponse(Tweet tweet);
}