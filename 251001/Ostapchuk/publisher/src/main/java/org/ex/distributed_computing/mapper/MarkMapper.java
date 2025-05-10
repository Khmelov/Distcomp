package org.ex.distributed_computing.mapper;

import org.ex.distributed_computing.dto.request.MarkRequestDTO;
import org.ex.distributed_computing.dto.response.MarkResponseDTO;
import org.ex.distributed_computing.model.Mark;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkMapper {

  Mark toEntity(MarkRequestDTO dto);

  MarkResponseDTO toDto(Mark mark);

  List<MarkResponseDTO> toDtoList(List<Mark> marks);
}

