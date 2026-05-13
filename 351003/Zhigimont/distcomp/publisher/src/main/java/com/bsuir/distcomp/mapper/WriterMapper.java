package com.bsuir.distcomp.mapper;

import com.bsuir.distcomp.dto.WriterRequestTo;
import com.bsuir.distcomp.dto.WriterResponseTo;
import com.bsuir.distcomp.entity.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    @Mapping(target = "topics", ignore = true)  // ← Игнорируем topics
    @Mapping(target = "role", expression = "java(dto.getRole() != null ? com.bsuir.distcomp.entity.Role.valueOf(dto.getRole().toUpperCase()) : com.bsuir.distcomp.entity.Role.CUSTOMER)")
    Writer toEntity(WriterRequestTo dto);

    @Mapping(target = "role", expression = "java(entity.getRole() != null ? entity.getRole().name() : null)")
    WriterResponseTo toDto(Writer entity);
}
