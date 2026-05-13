package org.example.mapper;

import org.example.dto.EditorRequestTo;
import org.example.dto.EditorResponseTo;
import org.example.model.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EditorMapper {
    Editor toEntity(EditorRequestTo requestTo);
    EditorResponseTo toResponse(Editor entity);
}