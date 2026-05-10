package com.adashkevich.redis.lab.mapper;

import com.adashkevich.redis.lab.dto.request.EditorRequestTo;
import com.adashkevich.redis.lab.dto.response.EditorResponseTo;
import com.adashkevich.redis.lab.model.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    Editor toEntity(EditorRequestTo dto);

    @Mapping(target = "id", source = "id")
    EditorResponseTo toResponse(Editor entity);
}
