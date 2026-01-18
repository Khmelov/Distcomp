package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.NoteRequestTo;
import com.rest.restapp.dto.response.NoteResponseTo;
import com.rest.restapp.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {

    Note toEntity(NoteRequestTo requestTo);

    @Mapping(source = "issue.id", target = "issueId")
    NoteResponseTo toResponseTo(Note entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(NoteRequestTo requestTo, @MappingTarget Note entity);
}