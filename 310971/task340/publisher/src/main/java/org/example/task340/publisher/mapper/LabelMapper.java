package org.example.task340.publisher.mapper;

import org.example.task340.publisher.dto.LabelRequestTo;
import org.example.task340.publisher.dto.LabelResponseTo;
import org.example.task340.publisher.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tweetLabels", ignore = true)
    Label toEntity(LabelRequestTo request);

    LabelResponseTo toDto(Label entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tweetLabels", ignore = true)
    void updateEntityFromDto(LabelRequestTo request, @MappingTarget Label entity);
}

