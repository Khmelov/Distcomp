package com.example.Task310.mapper;

import com.example.Task310.bean.Editor;
import com.example.Task310.dto.EditorRequestTo;
import com.example.Task310.dto.EditorResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    EditorResponseTo toResponse(Editor editor);
    Editor toEntity(EditorRequestTo request);
    void updateEntity(EditorRequestTo request, @MappingTarget Editor editor);
}