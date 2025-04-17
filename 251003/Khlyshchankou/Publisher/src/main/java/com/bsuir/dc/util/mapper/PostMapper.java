package com.bsuir.dc.util.mapper;

import com.bsuir.dc.dto.request.PostRequestTo;
import com.bsuir.dc.dto.response.PostResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    @Mapping(target = "id", expression = "java(postRequestTo.getId())")
    @Mapping(target = "topicId", expression = "java(postRequestTo.getTopicId())")
    @Mapping(target = "content", expression = "java(postRequestTo.getContent())")
    PostResponseTo toPostResponse(PostRequestTo postRequestTo);
}
