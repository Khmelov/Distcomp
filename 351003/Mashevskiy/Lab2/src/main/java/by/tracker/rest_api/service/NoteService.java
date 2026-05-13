package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.NoteRequestDto;
import by.tracker.rest_api.dto.NoteResponseDto;
import by.tracker.rest_api.entity.Note;
import by.tracker.rest_api.entity.Tweet;
import by.tracker.rest_api.exception.ResourceNotFoundException;
import by.tracker.rest_api.exception.ValidationException;
import by.tracker.rest_api.mapper.NoteMapper;
import by.tracker.rest_api.repository.NoteRepository;
import by.tracker.rest_api.repository.TweetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final TweetRepository tweetRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, TweetRepository tweetRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.tweetRepository = tweetRepository;
        this.noteMapper = noteMapper;
    }

    @Transactional(readOnly = true)
    public NoteResponseDto getById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + id, 40404));
        return noteMapper.toResponseDto(note);
    }

    @Transactional(readOnly = true)
    public Page<NoteResponseDto> getAll(Pageable pageable) {
        return noteRepository.findAll(pageable)
                .map(noteMapper::toResponseDto);
    }

    public NoteResponseDto create(NoteRequestDto dto) {
        validateNote(dto);

        Tweet tweet = tweetRepository.findById(dto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tweet not found with id: " + dto.getTweetId(), 40402));

        Note note = new Note();
        note.setTweet(tweet);
        note.setContent(dto.getContent());

        note = noteRepository.save(note);
        return noteMapper.toResponseDto(note);
    }

    public NoteResponseDto update(NoteRequestDto dto) {
        if (dto.getId() == null) {
            throw new ValidationException("ID is required for update", 40001);
        }

        validateNote(dto);

        Note existingNote = noteRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + dto.getId(), 40404));

        if (dto.getTweetId() != null && !existingNote.getTweet().getId().equals(dto.getTweetId())) {
            Tweet tweet = tweetRepository.findById(dto.getTweetId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tweet not found with id: " + dto.getTweetId(), 40402));
            existingNote.setTweet(tweet);
        }

        noteMapper.updateEntity(dto, existingNote);
        existingNote = noteRepository.save(existingNote);
        return noteMapper.toResponseDto(existingNote);
    }

    public void delete(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Note not found with id: " + id, 40404);
        }
        noteRepository.deleteById(id);
    }

    private void validateNote(NoteRequestDto dto) {
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ValidationException("Content cannot be empty", 40017);
        }
        if (dto.getContent().length() < 2 || dto.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 2 and 2048 characters", 40018);
        }
        if (dto.getTweetId() == null) {
            throw new ValidationException("Tweet ID is required", 40019);
        }
    }
}