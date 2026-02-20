package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.EditorRequestTo;
import by.bsuir.distcomp.dto.response.EditorResponseTo;
import by.bsuir.distcomp.entity.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    Editor toEntity(EditorRequestTo dto);
    EditorResponseTo toResponseDto(Editor entity);
    void updateEntityFromDto(EditorRequestTo dto, @MappingTarget Editor entity);
}
