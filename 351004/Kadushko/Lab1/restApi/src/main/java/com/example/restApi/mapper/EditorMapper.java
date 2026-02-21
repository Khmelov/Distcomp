package com.example.restApi.mapper;

import com.example.restApi.dto.request.EditorRequestTo;
import com.example.restApi.dto.response.EditorResponseTo;
import com.example.restApi.model.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    Editor toEntity(EditorRequestTo request);
    EditorResponseTo toDto(Editor editor);
    void updateEntity(@MappingTarget Editor editor, EditorRequestTo request);
}
