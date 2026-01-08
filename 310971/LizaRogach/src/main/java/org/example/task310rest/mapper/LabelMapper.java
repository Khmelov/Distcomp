package org.example.task310rest.mapper;

import org.example.task310rest.dto.LabelRequestTo;
import org.example.task310rest.dto.LabelResponseTo;
import org.example.task310rest.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    Label toEntity(LabelRequestTo request);

    LabelResponseTo toDto(Label entity);

    void updateEntityFromDto(LabelRequestTo request, @MappingTarget Label entity);
}


