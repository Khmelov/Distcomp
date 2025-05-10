package com.homel.user_stories.mapper;

import com.homel.user_stories.dto.LabelRequestTo;
import com.homel.user_stories.dto.LabelResponseTo;
import com.homel.user_stories.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LabelMapper {
    LabelMapper INSTANCE = Mappers.getMapper(LabelMapper.class);

    Label toEntity(LabelRequestTo dto);
    LabelResponseTo toResponse(Label entity);
}
