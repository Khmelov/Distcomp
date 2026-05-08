package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.EditorRequestTo;
import by.bsuir.distcomp.dto.response.EditorResponseTo;
import by.bsuir.distcomp.entity.Editor;
import by.bsuir.distcomp.model.EditorRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    Editor toEntity(EditorRequestTo dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    EditorResponseTo toResponseDto(Editor entity);

    @org.mapstruct.Named("roleToString")
    default String roleToString(EditorRole role) {
        return role == null ? null : role.name();
    }

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(EditorRequestTo dto, @MappingTarget Editor entity);
}
