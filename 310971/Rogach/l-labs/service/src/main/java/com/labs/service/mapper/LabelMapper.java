package com.labs.service.mapper;

import com.labs.domain.entity.Label;
import com.labs.service.dto.LabelDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelDto toDto(Label label);
    Label toEntity(LabelDto labelDto);
}

