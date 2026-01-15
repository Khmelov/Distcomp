package com.example.task320jpa.mapper;

import com.example.task320jpa.dto.request.NoteRequestTo;
import com.example.task320jpa.dto.response.NoteResponseTo;
import com.example.task320jpa.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct маппер для Note
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoteMapper {
    
    /**
     * Преобразовать NoteRequestTo в Note
     */
    Note toEntity(NoteRequestTo requestTo);
    
    /**
     * Преобразовать Note в NoteResponseTo
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tweetId", source = "tweetId")
    @Mapping(target = "content", source = "content")
    NoteResponseTo toResponseTo(Note note);
    
    /**
     * Обновить существующий Note из NoteRequestTo (для PATCH операций)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequestTo(NoteRequestTo requestTo, @MappingTarget Note note);
}
