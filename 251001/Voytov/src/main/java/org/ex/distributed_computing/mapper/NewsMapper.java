package org.ex.distributed_computing.mapper;

import java.util.List;
import org.ex.distributed_computing.dto.request.NewsRequestDTO;
import org.ex.distributed_computing.dto.response.NewsResponseDTO;
import org.ex.distributed_computing.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsMapper {

  @Mapping(source = "authorId", target = "author.id")
  News toEntity(NewsRequestDTO dto);

  @Mapping(source = "author.id", target = "authorId")
  NewsResponseDTO toDto(News news);

  List<NewsResponseDTO> toDtoList(List<News> newsList);
}

