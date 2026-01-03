package com.publisher.mapper;

import com.publisher.dto.request.TweetRequestTo;
import com.publisher.dto.response.TweetResponseTo;
import com.publisher.entity.Tweet;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TweetMapper {
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "modified", ignore = true)
	Tweet toEntity(TweetRequestTo dto);
	
	TweetResponseTo toResponse(Tweet entity);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "modified", ignore = true)
	void updateEntity(TweetRequestTo dto, @MappingTarget Tweet entity);
	
	@AfterMapping
	default void afterUpdate(@MappingTarget Tweet entity) {
		entity.setModified(java.time.LocalDateTime.now());
	}
}