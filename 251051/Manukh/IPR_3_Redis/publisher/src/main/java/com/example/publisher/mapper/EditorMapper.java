package com.example.publisher.mapper;

import com.example.publisher.dto.request.EditorRequestTo;
import com.example.publisher.dto.response.EditorResponseTo;
import com.example.publisher.entity.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EditorMapper {

    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "modifiedAt", target = "modifiedAt")
    EditorResponseTo toResponse(Editor editor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    Editor toEntity(EditorRequestTo request);

    List<EditorResponseTo> toResponseList(List<Editor> editors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    void updateEntity(EditorRequestTo request, @MappingTarget Editor editor);
}