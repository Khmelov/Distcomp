package com.example.publisher.mapper;

import com.example.publisher.dto.request.MarkRequestTo;
import com.example.publisher.dto.response.MarkResponseTo;
import com.example.publisher.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkMapper {

    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "modifiedAt", target = "modifiedAt")
    MarkResponseTo toResponse(Mark mark);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    Mark toEntity(MarkRequestTo request);

    List<MarkResponseTo> toResponseList(List<Mark> marks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    void updateEntity(MarkRequestTo request, @MappingTarget Mark mark);
}