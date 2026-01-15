package com.example.task310rest.mapper;

import com.example.task310rest.dto.request.TweetRequestTo;
import com.example.task310rest.dto.response.TweetResponseTo;
import com.example.task310rest.entity.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct маппер для Tweet
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TweetMapper {
    
    /**
     * Преобразовать TweetRequestTo в Tweet
     */
    Tweet toEntity(TweetRequestTo requestTo);
    
    /**
     * Преобразовать Tweet в TweetResponseTo
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    TweetResponseTo toResponseTo(Tweet tweet);
    
    /**
     * Обновить существующий Tweet из TweetRequestTo (для PATCH операций)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequestTo(TweetRequestTo requestTo, @MappingTarget Tweet tweet);
}
