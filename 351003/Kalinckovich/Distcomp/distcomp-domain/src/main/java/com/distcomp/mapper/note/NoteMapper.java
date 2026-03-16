package com.distcomp.mapper.note;

import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.mapper.config.MappedConfig;
import com.distcomp.model.note.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappedConfig.class)
public interface NoteMapper {

    NoteResponseDto toResponse(Note entity);

    @Mapping(target = "id", ignore = true)
    Note toEntity(NoteCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topicId", ignore = true)
    Note updateFromDto(NoteUpdateRequest dto, @MappingTarget Note entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "topicId", target = "topicId")
    Note updateFromPatch(NotePatchRequest dto, @MappingTarget Note entity);
}
