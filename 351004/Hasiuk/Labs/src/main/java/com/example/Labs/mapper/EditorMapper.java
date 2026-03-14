package com.example.Labs.mapper;

import com.example.Labs.dto.request.EditorRequestTo;
import com.example.Labs.dto.response.EditorResponseTo;
import com.example.Labs.entity.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    Editor toEntity(EditorRequestTo dto);
    EditorResponseTo toDto(Editor entity);
    void updateEntity(EditorRequestTo dto, @MappingTarget Editor entity);
}