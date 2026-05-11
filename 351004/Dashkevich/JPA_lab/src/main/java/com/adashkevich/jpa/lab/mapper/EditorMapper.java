package com.adashkevich.jpa.lab.mapper;

import com.adashkevich.jpa.lab.dto.request.EditorRequestTo;
import com.adashkevich.jpa.lab.dto.response.EditorResponseTo;
import com.adashkevich.jpa.lab.model.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    Editor toEntity(EditorRequestTo dto);

    @Mapping(target = "id", source = "id")
    EditorResponseTo toResponse(Editor entity);
}
