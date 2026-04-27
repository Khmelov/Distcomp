package com.lizaveta.notebook.mapper;

import com.lizaveta.notebook.model.UserRole;
import com.lizaveta.notebook.model.dto.request.WriterRequestTo;
import com.lizaveta.notebook.model.dto.response.WriterResponseTo;
import com.lizaveta.notebook.model.entity.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WriterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(com.lizaveta.notebook.model.UserRole.CUSTOMER)")
    Writer toEntity(WriterRequestTo request);

    @Mapping(target = "role", source = "role", qualifiedByName = "userRoleName")
    WriterResponseTo toResponse(Writer entity);

    @Named("userRoleName")
    default String userRoleName(final UserRole role) {
        if (role == null) {
            return UserRole.CUSTOMER.name();
        }
        return role.name();
    }
}
