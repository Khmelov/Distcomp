// MarkMapper.java
package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.dto.request.MarkRequestTo;
import com.example.dto.response.MarkResponseTo;
import com.example.model.Mark;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "storyIds", ignore = true)
    Mark toEntity(MarkRequestTo request);

    MarkResponseTo toResponse(Mark mark);

    List<MarkResponseTo> toResponseList(List<Mark> marks);
}