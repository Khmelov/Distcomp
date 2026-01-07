package com.task310.blogplatform.mapper;

import com.task310.blogplatform.dto.PostRequestTo;
import com.task310.blogplatform.dto.PostResponseTo;
import com.task310.blogplatform.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "article", ignore = true)
    Post toEntity(PostRequestTo dto);

    PostResponseTo toResponseDto(Post entity);

    List<PostResponseTo> toResponseDtoList(List<Post> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "article", ignore = true)
    void updateEntityFromDto(PostRequestTo dto, @MappingTarget Post entity);
}

