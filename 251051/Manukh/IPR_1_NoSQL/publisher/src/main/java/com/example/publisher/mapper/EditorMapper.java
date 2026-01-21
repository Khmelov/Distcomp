package com.example.publisher.mapper;

import com.example.publisher.dto.request.EditorRequestTo;
import com.example.publisher.dto.response.EditorResponseTo;
import com.example.publisher.entity.Editor;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EditorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    Editor toEntity(EditorRequestTo request);

    @Mapping(source = "createdAt", target = "created")
    @Mapping(source = "modifiedAt", target = "modified")
    EditorResponseTo toResponse(Editor editor);

    List<EditorResponseTo> toResponseList(List<Editor> editors);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    void updateEntity(EditorRequestTo request, @MappingTarget Editor editor);
}