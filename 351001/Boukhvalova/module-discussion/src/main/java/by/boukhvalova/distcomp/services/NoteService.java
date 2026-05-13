package by.boukhvalova.distcomp.services;

import by.boukhvalova.distcomp.entities.Note;
import by.boukhvalova.distcomp.repositories.NoteRepository;
import by.boukhvalova.distcomp.dto.NoteMapper;
import by.boukhvalova.distcomp.dto.NoteRequestTo;
import by.boukhvalova.distcomp.dto.NoteResponseTo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NoteService {
    private static final String APPROVED_STATE = "APPROVE";
    private static final String DECLINED_STATE = "DELCINE";
    private static final List<String> STOP_WORDS = List.of("spam", "scam", "fraud", "hate");

    public final NoteRepository repImpl;
    public final NoteMapper mapper;

    public List<NoteResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }

    public NoteResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    public NoteResponseTo create(NoteRequestTo req) {
        Note note = mapper.in(req);
        note.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
        note.setCountry(moderate(note.getContent()));
        return repImpl.create(note).map(mapper::out)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public NoteResponseTo update(NoteRequestTo req) {
        if (repImpl.get(req.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        Note note = mapper.in(req);
        note.setCountry(moderate(note.getContent()));
        return repImpl.update(note).map(mapper::out)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public void delete(Long id) {
        repImpl.delete(id);
    }

    private String moderate(String content) {
        String normalized = content == null ? "" : content.toLowerCase();
        return STOP_WORDS.stream().anyMatch(normalized::contains) ? DECLINED_STATE : APPROVED_STATE;
    }
}
