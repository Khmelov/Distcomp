package com.group310971.gormash.mapper;

import com.group310971.gormash.dto.TopicRequestTo;
import com.group310971.gormash.dto.TopicResponseTo;
import com.group310971.gormash.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TopicMapper {
    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    Topic toEntity(TopicRequestTo dto);

    @Mapping(target = "editorId", source = "editor.id")
    TopicResponseTo toResponse(Topic entity);
}
