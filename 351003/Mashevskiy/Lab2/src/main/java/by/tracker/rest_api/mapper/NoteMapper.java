package by.tracker.rest_api.mapper;

import by.tracker.rest_api.dto.NoteRequestDto;
import by.tracker.rest_api.dto.NoteResponseDto;
import by.tracker.rest_api.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponseDto toResponseDto(Note entity) {
        if (entity == null) return null;
        NoteResponseDto dto = new NoteResponseDto();
        dto.setId(entity.getId());
        dto.setTweetId(entity.getTweet() != null ? entity.getTweet().getId() : null);
        dto.setContent(entity.getContent());
        return dto;
    }

    public void updateEntity(NoteRequestDto dto, Note entity) {
        if (dto.getContent() != null) entity.setContent(dto.getContent());
    }
}