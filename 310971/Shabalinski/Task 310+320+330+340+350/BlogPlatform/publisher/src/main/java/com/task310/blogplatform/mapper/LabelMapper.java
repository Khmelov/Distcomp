package com.task310.blogplatform.mapper;

import com.task310.blogplatform.dto.LabelRequestTo;
import com.task310.blogplatform.dto.LabelResponseTo;
import com.task310.blogplatform.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "articles", ignore = true)
    Label toEntity(LabelRequestTo dto);

    LabelResponseTo toResponseDto(Label entity);

    List<LabelResponseTo> toResponseDtoList(List<Label> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "articles", ignore = true)
    void updateEntityFromDto(LabelRequestTo dto, @MappingTarget Label entity);
}

