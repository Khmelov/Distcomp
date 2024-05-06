package by.bsuir.dc.lab5.dto.mappers;

import by.bsuir.dc.lab5.dto.EditorRequestTo;
import by.bsuir.dc.lab5.dto.EditorResponseTo;
import by.bsuir.dc.lab5.entities.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    EditorMapper instance = Mappers.getMapper(EditorMapper.class);
    Editor convertFromDTO(EditorRequestTo dto);
    EditorResponseTo convertRequestToDTO(EditorRequestTo dto);
    EditorResponseTo convertToDTO(Editor editor);
}
