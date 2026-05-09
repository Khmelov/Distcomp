package com.example.Labs.mapper;
import com.example.Labs.dto.request.EditorRequestTo;
import com.example.Labs.dto.response.EditorResponseTo;
import com.example.Labs.entity.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Пароль шифруем вручную в сервисе
    Editor toEntity(EditorRequestTo dto);

    EditorResponseTo toDto(Editor entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(EditorRequestTo dto, @MappingTarget Editor entity);
}