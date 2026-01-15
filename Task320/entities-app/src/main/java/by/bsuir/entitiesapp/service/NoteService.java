package by.bsuir.entitiesapp.service;

import by.bsuir.entitiesapp.dto.NoteRequestTo;
import by.bsuir.entitiesapp.dto.NoteResponseTo;
import by.bsuir.entitiesapp.entity.Note;
import by.bsuir.entitiesapp.exception.BadRequestException;
import by.bsuir.entitiesapp.exception.NotFoundException;
import by.bsuir.entitiesapp.repository.NoteRepository;
import by.bsuir.entitiesapp.repository.TweetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository repository;
    private final TweetRepository tweetRepository;

    public NoteService(NoteRepository repository, TweetRepository tweetRepository) {
        this.repository = repository;
        this.tweetRepository = tweetRepository;
    }

    @Transactional
    public NoteResponseTo create(NoteRequestTo dto) {
        validate(dto);

        if (!tweetRepository.existsById(dto.tweetId)) {
            throw new BadRequestException("Invalid tweet", "40002");
        }

        Note note = new Note();
        note.setContent(dto.content);
        note.setTweetId(dto.tweetId);

        return toResponse(repository.save(note));
    }

    public NoteResponseTo get(Long id) {
        Note note = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found", "40401"));
        return toResponse(note);
    }

    public List<NoteResponseTo> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoteResponseTo update(Long id, NoteRequestTo dto) {
        validate(dto);

        Note note = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found", "40401"));

        if (!tweetRepository.existsById(dto.tweetId)) {
            throw new BadRequestException("Invalid tweet", "40002");
        }

        note.setContent(dto.content);
        note.setTweetId(dto.tweetId);

        return toResponse(repository.save(note));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Note not found", "40401");
        }
        repository.deleteById(id);
    }

    private void validate(NoteRequestTo dto) {
        if (dto.content == null || dto.content.isBlank() ||
            dto.tweetId == null) {
            throw new BadRequestException("Invalid fields", "40001");
        }

        // Additional validation rules
        if (dto.content.length() < 2 || dto.content.length() > 2048) {
            throw new BadRequestException("Invalid content length", "40001");
        }
    }

    private NoteResponseTo toResponse(Note note) {
        NoteResponseTo dto = new NoteResponseTo();
        dto.id = note.getId();
        dto.content = note.getContent();
        dto.tweetId = note.getTweetId();
        return dto;
    }
}
