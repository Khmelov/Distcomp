package com.jpa.mapper;

import com.jpa.dto.request.NoteRequestTo;
import com.jpa.dto.response.NoteResponseTo;
import com.jpa.entity.Note;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NoteMapper {
	
	@Mapping(target = "id", ignore = true)
	Note toEntity(NoteRequestTo dto);
	
	NoteResponseTo toResponse(Note entity);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	void updateEntity(NoteRequestTo dto, @MappingTarget Note entity);
}