package com.rest.mapper;

import com.rest.dto.request.LabelRequestTo;
import com.rest.dto.response.LabelResponseTo;
import com.rest.entity.Label;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    
	@Mapping(target = "id", ignore = true)
    Label toEntity(LabelRequestTo dto);
    
    LabelResponseTo toResponse(Label entity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
    void updateEntity(LabelRequestTo dto, @MappingTarget Label entity);
}