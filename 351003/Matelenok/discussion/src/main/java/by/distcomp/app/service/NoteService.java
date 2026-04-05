package by.distcomp.app.service;

import by.distcomp.app.dto.NoteRequestTo;
import by.distcomp.app.dto.NoteResponseTo;
import by.distcomp.app.mapper.NoteMapper;
import by.distcomp.app.model.Note;
import by.distcomp.app.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    public NoteResponseTo createNote(NoteRequestTo dto) {
        Note note = noteMapper.toEntity(dto);

        if (note.getId() == null) {
            note.setId(System.currentTimeMillis());
        }

        Note saved = noteRepository.save(note);
        return noteMapper.toResponse(saved);
    }

    public NoteResponseTo updateNote(NoteRequestTo dto) {
        Note note = noteRepository.findById(dto.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + dto.id()));

        note.setContent(dto.content());
        note.setArticleId(dto.articleId());

        Note saved = noteRepository.save(note);
        return noteMapper.toResponse(saved);
    }

    public NoteResponseTo getNoteById(Long id) {
        return noteRepository.findById(id)
                .map(noteMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    public void deleteNoteById(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        noteRepository.deleteById(id);
    }

    public List<NoteResponseTo> getNotesByArticleId(Long articleId) {
        return noteRepository.findByArticleId(articleId)
                .stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    public List<NoteResponseTo> getAllNotes() {
        return noteRepository.findAll()
                .stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    public void deleteNotesByArticleId(Long articleId) {

        List<Note> notes = noteRepository.findByArticleId(articleId);

        if (!notes.isEmpty()) {
            noteRepository.deleteAll(notes);
        }
    }
}