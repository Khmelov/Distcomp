package com.rest.mapper;

import com.rest.dto.request.WriterRequestTo;
import com.rest.dto.response.WriterResponseTo;
import com.rest.entity.Writer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WriterMapper {
    
	@Mapping(target = "id", ignore = true)
    Writer toEntity(WriterRequestTo dto);
    
    WriterResponseTo toResponse(Writer entity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
    void updateEntity(WriterRequestTo dto, @MappingTarget Writer entity);
}