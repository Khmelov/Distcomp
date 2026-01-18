package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.NoteRequestToDto;
import com.rest.restapp.dto.response.NoteResponseTo;
import com.rest.restapp.entity.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {

    Notice toEntity(NoteRequestToDto requestTo);

    @Mapping(source = "issue.id", target = "issueId")
    NoteResponseTo toResponseTo(Notice entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(NoteRequestToDto requestTo, @MappingTarget Notice entity);
}