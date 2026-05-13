package org.rv.lab1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rv.lab1.domain.Editor;
import org.rv.lab1.dto.EditorRegisterRequestTo;
import org.rv.lab1.dto.EditorRequestTo;
import org.rv.lab1.dto.EditorResponseTo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EditorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "CUSTOMER")
    Editor toEntity(EditorRequestTo dto);

    @Mapping(target = "role", ignore = true)
    void updateEntity(EditorRequestTo dto, @MappingTarget Editor target);
    EditorResponseTo toResponse(Editor editor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "firstName", target = "firstname")
    @Mapping(source = "lastName", target = "lastname")
    Editor toEntity(EditorRegisterRequestTo dto);
}