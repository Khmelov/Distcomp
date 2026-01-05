package com.publisher.mapper;

import com.publisher.dto.request.WriterRequestTo;
import com.publisher.dto.response.WriterResponseTo;
import com.publisher.entity.Writer;
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