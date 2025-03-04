package com.bsuir.dc.util.mapper;

import com.bsuir.dc.dto.request.PostRequestTo;
import com.bsuir.dc.dto.response.PostResponseTo;
import com.bsuir.dc.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    @Mapping(target = "topicId", expression = "java(post.getTopic().getId())")
    PostResponseTo toPostResponse(Post post);
    List<PostResponseTo> toPostResponseList(List<Post> posts);
    Post toPost(PostRequestTo postRequestTo);
}
