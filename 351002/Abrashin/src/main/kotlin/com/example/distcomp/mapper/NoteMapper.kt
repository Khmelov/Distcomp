package com.example.distcomp.mapper

import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.model.Note
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface NoteMapper {
    fun toEntity(request: NoteRequestTo): Note
    fun toResponse(entity: Note): NoteResponseTo
}
