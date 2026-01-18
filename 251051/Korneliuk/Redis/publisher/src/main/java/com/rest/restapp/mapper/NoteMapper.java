package com.rest.restapp.mapper;

import com.common.NoteMessage;
import com.common.NoteResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {

    NoteResponseTo mapToResponse(NoteMessage message);
}