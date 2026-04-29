package com.example.task310.mapper;

import com.example.task310.dto.MarkRequestTo;
import com.example.task310.dto.MarkResponseTo;
import com.example.task310.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    Mark toEntity(MarkRequestTo request);

    MarkResponseTo toResponse(Mark mark);
}