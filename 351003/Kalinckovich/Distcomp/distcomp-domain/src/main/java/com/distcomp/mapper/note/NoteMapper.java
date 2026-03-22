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

import java.util.UUID;


@Mapper(config = MappedConfig.class)
public interface NoteMapper {

    @Mapping(target = "id", source = "key.id")
    @Mapping(target = "topicId", source = "key.topicId")
    NoteResponseDto toResponse(Note entity);

    @Mapping(target = "key", ignore = true)
    Note toEntity(NoteCreateRequest dto);

    @Mapping(target = "key", ignore = true)
    Note updateFromDto(NoteUpdateRequest dto, @MappingTarget Note entity);

    @Mapping(target = "key", ignore = true)
    Note updateFromPatch(NotePatchRequest dto, @MappingTarget Note entity);

    default Note toEntityWithKey(final NoteCreateRequest dto, final String country, final Long id) {
        final Note note = toEntity(dto);
        final Note.NoteKey key = new Note.NoteKey(country, dto.getTopicId(), id);
        note.setKey(key);
        return note;
    }
}
