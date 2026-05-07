package com.distcomp.discussion.service;

import com.distcomp.discussion.dto.NoteRequestDTO;
import com.distcomp.discussion.dto.NoteResponseDTO;
import com.distcomp.discussion.exception.NoteNotFoundException;
import com.distcomp.discussion.model.Note;
import com.distcomp.discussion.model.NoteKey;
import com.distcomp.discussion.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private static final AtomicLong idGenerator = new AtomicLong(1);

    @Autowired
    private NoteRepository noteRepository;

    public List<NoteResponseDTO> getAllNotes() {
        return noteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteResponseDTO> getNotesByTweetId(Long tweetId) {
        return noteRepository.findByIdTweetId(tweetId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NoteResponseDTO getNote(Long tweetId, Long id) {
        Optional<Note> note = noteRepository.findByIdTweetIdAndId(tweetId, id);
        if (note.isEmpty()) {
            throw new NoteNotFoundException("Note not found with tweetId: " + tweetId + " and id: " + id);
        }
        return convertToDTO(note.get());
    }

    public NoteResponseDTO createNote(NoteRequestDTO request) {
        Long noteId = request.getId() != null ? request.getId() : idGenerator.getAndIncrement();
        NoteKey key = new NoteKey(request.getTweetId(), noteId);

        Note note = new Note();
        note.setId(key);
        note.setContent(request.getContent());

        Note savedNote = noteRepository.save(note);
        return convertToDTO(savedNote);
    }

    public NoteResponseDTO updateNote(Long tweetId, Long id, NoteRequestDTO request) {
        Optional<Note> existingNote = noteRepository.findByIdTweetIdAndId(tweetId, id);
        if (existingNote.isEmpty()) {
            throw new NoteNotFoundException("Note not found with tweetId: " + tweetId + " and id: " + id);
        }

        Note note = existingNote.get();
        note.setContent(request.getContent());

        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    public void deleteNote(Long tweetId, Long id) {
        Optional<Note> note = noteRepository.findByIdTweetIdAndId(tweetId, id);
        if (note.isEmpty()) {
            throw new NoteNotFoundException("Note not found with tweetId: " + tweetId + " and id: " + id);
        }
        String country = note.get().getId().getCountry();
        noteRepository.deleteByCountryAndTweetIdAndId(country, tweetId, id);
    }

    private NoteResponseDTO convertToDTO(Note note) {
        return new NoteResponseDTO(
                note.getId().getId(),
                note.getId().getTweetId(),
                note.getContent()
        );
    }
}