package org.ex.distributed_computing.mapper;

import java.util.List;
import org.ex.distributed_computing.dto.request.NoticeRequestDTO;
import org.ex.distributed_computing.dto.response.NoticeResponseDTO;
import org.ex.distributed_computing.model.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoticeMapper {

  @Mapping(source = "newsId", target = "news.id")
  Notice toEntity(NoticeRequestDTO dto);

  @Mapping(source = "news.id", target = "newsId")
  NoticeResponseDTO toDto(Notice notice);

  List<NoticeResponseDTO> toDtoList(List<Notice> notices);
}

