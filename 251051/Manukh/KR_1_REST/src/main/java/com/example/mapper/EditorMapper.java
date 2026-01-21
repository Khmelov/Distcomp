// EditorMapper.java
package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.dto.request.EditorRequestTo;
import com.example.dto.response.EditorResponseTo;
import com.example.model.Editor;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Editor toEntity(EditorRequestTo request);

    EditorResponseTo toResponse(Editor editor);

    List<EditorResponseTo> toResponseList(List<Editor> editors);
}