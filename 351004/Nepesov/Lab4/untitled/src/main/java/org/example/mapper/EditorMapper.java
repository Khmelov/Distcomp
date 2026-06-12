package org.example.mapper;

import org.example.dto.EditorRequestTo;
import org.example.dto.EditorResponseTo;
import org.example.model.Editor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EditorMapper {

    Editor toEntity(EditorRequestTo request);

    EditorResponseTo toResponse(Editor entity);
}