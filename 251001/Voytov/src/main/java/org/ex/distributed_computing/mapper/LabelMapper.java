package org.ex.distributed_computing.mapper;

import java.util.List;
import org.ex.distributed_computing.dto.request.LabelRequestDTO;
import org.ex.distributed_computing.dto.response.LabelResponseDTO;
import org.ex.distributed_computing.model.Label;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LabelMapper {

  Label toEntity(LabelRequestDTO dto);

  LabelResponseDTO toDto(Label label);

  List<LabelResponseDTO> toDtoList(List<Label> labels);
}

