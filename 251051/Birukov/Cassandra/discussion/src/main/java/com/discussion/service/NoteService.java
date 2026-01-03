package com.discussion.service;

import com.discussion.entity.Note;
import com.discussion.dto.request.NoteRequestTo;
import com.discussion.dto.request.NoteResponseTo;
import com.discussion.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    
    private final NoteRepository noteRepository;
    
	public NoteService(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}
	
    public Note createNote(NoteRequestTo request) {
        Note.NoteKey key = new Note.NoteKey("RU", request.getTweetId(), 10L);
		Note note = new Note(key, request.getContent());
        return noteRepository.save(note);
    }
    
    public Note getNote(String country, Long tweetId, Long id) {
        return noteRepository.findByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Note not found with country=%s, tweetId=%d and id=%d", country, tweetId, id)));
    }
	
	public Note getNoteById(Long id) {
		return noteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Note not found with id=%d", id)));
    }
    
    public List<Note> getNotesByCountry(String country) {
        return noteRepository.findByKeyCountry(country);
    }
    
    public List<Note> getAllNotes() {
		List<Note> notes = noteRepository.findAll();
		return notes;
    }
    
    public Note updateNote(Long id, NoteRequestTo request) {
        Note existingNote = getNoteById(id);
        existingNote.setContent(request.getContent());
        
        return noteRepository.save(existingNote);
    }
    
    public void deleteNote(Long id) {
        Note note = getNoteById(id);
		Note.NoteKey key = note.getKey();
        noteRepository.deleteByCountryAndId(key.getCountry(), id);
    }
	
    public NoteResponseTo NoteToResponse(Note note) {
		Note.NoteKey key = note.getKey();
		NoteResponseTo response = new NoteResponseTo(key.getId(),
													 key.getTweetId(),
													 note.getContent());
        return response;
	}
	
	public List<NoteResponseTo> NotesToResponses(List<Note> notes) {
		List<NoteResponseTo> responses = new ArrayList<>();
		for (Note note : notes) {
			Note.NoteKey key = note.getKey();
			NoteResponseTo response = new NoteResponseTo(key.getId(),
														 key.getTweetId(),
														 note.getContent());
			responses.add(response);
		}
        return responses;
	}
    
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}