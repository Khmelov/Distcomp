package by.rest.mapper;

import by.rest.domain.Editor;
import by.rest.dto.EditorRequestTo;
import by.rest.dto.EditorResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    
    @Mapping(target = "id", ignore = true)
    Editor toEntity(EditorRequestTo dto);
    
    EditorResponseTo toResponse(Editor entity);
}