package org.polozkov.mapper.label;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.polozkov.dto.label.LabelRequestTo;
import org.polozkov.dto.label.LabelResponseTo;
import org.polozkov.entity.label.Label;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    LabelResponseTo labelToResponseDto(Label label);

    @Mapping(target = "id", ignore = true)
    Label requestDtoToLabel(LabelRequestTo labelRequest);
}
