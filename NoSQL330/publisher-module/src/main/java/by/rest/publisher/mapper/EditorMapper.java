package by.rest.publisher.mapper;

import by.rest.publisher.domain.Editor;
import by.rest.publisher.dto.EditorRequestTo;
import by.rest.publisher.dto.EditorResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stories", ignore = true)
    Editor toEntity(EditorRequestTo dto);
    
    EditorResponseTo toResponse(Editor entity);
}