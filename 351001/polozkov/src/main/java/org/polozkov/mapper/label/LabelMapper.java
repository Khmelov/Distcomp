package org.polozkov.mapper.label;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.polozkov.dto.label.LabelRequestTo;
import org.polozkov.dto.label.LabelResponseTo;
import org.polozkov.entity.label.Label;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    LabelResponseTo labelToResponseDto(Label label);

    @Mapping(target = "id", ignore = true)
    Label requestDtoToLabel(LabelRequestTo labelRequest);

    @Mapping(target = "id", ignore = true)
    Label updateLabel(@MappingTarget Label label, LabelRequestTo labelRequest);
}
