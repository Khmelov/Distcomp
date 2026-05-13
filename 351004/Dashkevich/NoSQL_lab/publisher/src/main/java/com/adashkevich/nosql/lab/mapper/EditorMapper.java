package com.adashkevich.nosql.lab.mapper;

import com.adashkevich.nosql.lab.dto.request.EditorRequestTo;
import com.adashkevich.nosql.lab.dto.response.EditorResponseTo;
import com.adashkevich.nosql.lab.model.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    Editor toEntity(EditorRequestTo dto);

    @Mapping(target = "id", source = "id")
    EditorResponseTo toResponse(Editor entity);
}
