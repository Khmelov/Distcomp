package com.group310971.gormash.mapper;

import com.group310971.gormash.dto.EditorRequestTo;
import com.group310971.gormash.dto.EditorResponseTo;
import com.group310971.gormash.model.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EditorMapper {
    EditorMapper INSTANCE = Mappers.getMapper(EditorMapper.class);

    Editor toEntity(EditorRequestTo dto);

    EditorResponseTo toResponse(Editor entity);
}
