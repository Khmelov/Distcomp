package org.ex.distributed_computing.mapper;

import org.ex.distributed_computing.dto.request.PostRequestDTO;
import org.ex.distributed_computing.dto.response.PostResponseDTO;
import org.ex.distributed_computing.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mapping(source = "tweetId", target = "tweet.id")
  Post toEntity(PostRequestDTO dto);

  @Mapping(source = "tweet.id", target = "tweetId")
  PostResponseDTO toDto(Post post);

  List<PostResponseDTO> toDtoList(List<Post> posts);
}

