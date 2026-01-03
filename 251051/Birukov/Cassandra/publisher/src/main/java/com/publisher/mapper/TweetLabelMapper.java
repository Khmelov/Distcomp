package com.publisher.mapper;

import com.publisher.dto.request.TweetLabelRequestTo;
import com.publisher.dto.response.TweetLabelResponseTo;
import com.publisher.entity.TweetLabel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TweetLabelMapper {
	
	@Mapping(target = "id", ignore = true)
	TweetLabel toEntity(TweetLabelRequestTo dto);
	
	TweetLabelResponseTo toResponse(TweetLabel entity);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	void updateEntity(TweetLabelRequestTo dto, @MappingTarget TweetLabel entity);
}