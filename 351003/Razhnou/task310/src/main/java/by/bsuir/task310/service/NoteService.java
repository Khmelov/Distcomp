package by.bsuir.task310.service;

import by.bsuir.task310.dto.request.NoteRequestTo;
import by.bsuir.task310.dto.response.NoteResponseTo;
import by.bsuir.task310.entity.Note;
import by.bsuir.task310.exception.BadRequestException;
import by.bsuir.task310.exception.NotFoundException;
import by.bsuir.task310.mapper.NoteMapper;
import by.bsuir.task310.repository.NoteRepository;
import by.bsuir.task310.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final StoryRepository storyRepository;

    public NoteService(NoteRepository noteRepository, StoryRepository storyRepository) {
        this.noteRepository = noteRepository;
        this.storyRepository = storyRepository;
    }

    public NoteResponseTo create(NoteRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Reaction id must be null on create", 3);
        }
        validateId(request.storyId(), "Tweet id");
        requireStoryExists(request.storyId());
        validateText(request.content(), "Reaction content");

        Note note = NoteMapper.toEntity(request);
        return NoteMapper.toResponse(noteRepository.save(note));
    }

    public List<NoteResponseTo> findAll() {
        return noteRepository.findAll().stream()
                .map(NoteMapper::toResponse)
                .toList();
    }

    public NoteResponseTo findById(Long id) {
        validateId(id, "Reaction id");
        return NoteMapper.toResponse(getNote(id));
    }

    public NoteResponseTo update(NoteRequestTo request) {
        validateId(request.id(), "Reaction id");
        validateId(request.storyId(), "Tweet id");
        requireStoryExists(request.storyId());
        validateText(request.content(), "Reaction content");
        getNote(request.id());

        Note note = NoteMapper.toEntity(request);
        return NoteMapper.toResponse(noteRepository.update(note));
    }

    public void delete(Long id) {
        validateId(id, "Reaction id");
        getNote(id);
        noteRepository.deleteById(id);
    }

    public List<NoteResponseTo> findByStoryId(Long storyId) {
        validateId(storyId, "Tweet id");
        if (!storyRepository.existsById(storyId)) {
            throw new NotFoundException("Tweet not found", 2);
        }
        return noteRepository.findByStoryId(storyId).stream()
                .map(NoteMapper::toResponse)
                .toList();
    }

    private Note getNote(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reaction not found", 4));
    }

    private void requireStoryExists(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new BadRequestException("Tweet with id " + storyId + " does not exist", 5);
        }
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " must not be blank", 2);
        }
    }
}
