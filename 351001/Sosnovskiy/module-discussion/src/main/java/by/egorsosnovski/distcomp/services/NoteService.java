package by.egorsosnovski.distcomp.services;

import by.egorsosnovski.distcomp.dto.*;
import by.egorsosnovski.distcomp.entities.Note;
import by.egorsosnovski.distcomp.entities.NoteKey;
import by.egorsosnovski.distcomp.repositories.NoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
public class NoteService {
    public final NoteRepository repImpl;
    @Qualifier("noteMapper")
    public final NoteMapper mapper;
    private final WebClient webClient;

    public List<NoteResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }
    public NoteResponseTo getById(Long id) {
        NoteKey key = new NoteKey();
        key.setId(id);
        key.setCountry("by");
        return repImpl.get(key).map(mapper::out).orElseThrow();
    }
    public NoteResponseTo create(NoteRequestTo req) {
        Note note = mapper.in(req);
        return repImpl.create(note).map(mapper::out).orElseThrow();
    }
    public NoteResponseTo update(NoteRequestTo req) {
        Note note = mapper.in(req);
        return repImpl.update(note).map(mapper::out).orElseThrow();
    }
    public void delete(Long id) {
        NoteKey key = new NoteKey();
        key.setId(id);
        key.setCountry("by");
        repImpl.delete(key);
    }
}

