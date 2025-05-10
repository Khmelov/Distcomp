package org.ex.distributed_computing.mapper;

import org.ex.distributed_computing.dto.request.TweetRequestDTO;
import org.ex.distributed_computing.dto.response.TweetResponseDTO;
import org.ex.distributed_computing.model.Tweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TweetMapper {

  @Mapping(source = "userId", target = "user.id")
  Tweet toEntity(TweetRequestDTO dto);

  @Mapping(source = "user.id", target = "userId")
  TweetResponseDTO toDto(Tweet tweet);

  List<TweetResponseDTO> toDtoList(List<Tweet> tweetList);
}

