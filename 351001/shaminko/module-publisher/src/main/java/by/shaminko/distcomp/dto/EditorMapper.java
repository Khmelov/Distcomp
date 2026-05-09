package by.shaminko.distcomp.dto;

import by.shaminko.distcomp.entities.Editor;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EditorMapper {

    EditorResponseTo out(Editor editor);

    Editor in(EditorRequestTo editor);
}
