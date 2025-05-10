package com.example.discussion.service;

import com.example.discussion.dto.NoteRequestTo;
import com.example.discussion.dto.NoteResponseTo;
import com.example.discussion.mapper.NoteMapper;
import com.example.discussion.model.Note;
import com.example.discussion.repository.NoteRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper = Mappers.getMapper(NoteMapper.class);
    private final SequenceGeneratorService sequenceGenerator;

    public NoteService(NoteRepository noteRepository, SequenceGeneratorService sequenceGenerator) {
        this.noteRepository = noteRepository;
        this.sequenceGenerator = sequenceGenerator;
    }

    public List<NoteResponseTo> findAll() {
        return noteRepository.findAll().stream()
                .map(noteMapper::toDto)
                .collect(Collectors.toList());
    }

    public NoteResponseTo findById(Long id) {
        Optional<Note> note = noteRepository.findById(id);
        return note.map(noteMapper::toDto).orElse(null);
    }


    public NoteResponseTo save(NoteRequestTo noteRequestTo) {
        Note note = noteMapper.toEntity(noteRequestTo);
        note.setId(sequenceGenerator.generateSequence("note_sequence"));
        Note savedNote = noteRepository.save(note);
        return noteMapper.toDto(savedNote);
    }

    public NoteResponseTo update(NoteRequestTo noteRequestTo) {
        Note existingNote = noteRepository.findById(noteRequestTo.getId()).orElseThrow(() -> new RuntimeException("Note not found"));
        noteMapper.updateEntityFromDto(noteRequestTo, existingNote);
        Note updatedNote = noteRepository.save(existingNote);
        return noteMapper.toDto(updatedNote);
    }

    public NoteResponseTo deleteById(Long id) {
        noteRepository.deleteById(id);
        return new NoteResponseTo();
    }
}