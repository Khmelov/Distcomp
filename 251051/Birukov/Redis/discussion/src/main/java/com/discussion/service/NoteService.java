package com.discussion.service;

import com.discussion.entity.Note;
import com.discussion.dto.request.NoteRequestTo;
import com.discussion.dto.response.NoteResponseTo;
import com.discussion.repository.NoteRepository;
import com.discussion.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    
    private final NoteRepository noteRepository;
    
	public NoteService(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}
	
	private static final List<String> STOP_WORDS = Arrays.asList(
        "spam", "scam", "hate", "violence", "illegal", "fraud"
    );
	
    public Note createNote(NoteRequestTo request) {
        Note.NoteKey key = new Note.NoteKey("RU", request.getTweetId(), request.getId());
		Note note = new Note(key, request.getContent());
		note.setState(Note.NoteState.PENDING);
        return noteRepository.save(note);
    }
    
    public Note getNote(String country, Long tweetId, Long id) {
        return noteRepository.findByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)
            .orElseThrow(() -> new NotFoundException(
                String.format("Note not found with country=%s, tweetId=%d and id=%d", country, tweetId, id)));
    }
	
	public Note getNoteById(Long id) {
		return noteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(
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
		Note.NoteState newState = performModeration(request.getContent());
		existingNote.setState(newState);
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
		response.setState(note.getState());
        return response;
	}
	
	public List<NoteResponseTo> NotesToResponses(List<Note> notes) {
		List<NoteResponseTo> responses = new ArrayList<>();
		for (Note note : notes) {
			Note.NoteKey key = note.getKey();
			NoteResponseTo response = new NoteResponseTo(key.getId(),
														 key.getTweetId(),
														 note.getContent());
			response.setState(note.getState());
			responses.add(response);
		}
        return responses;
	}
	
	public NoteRequestTo NoteToRequest(Note note) {
		Note.NoteKey key = note.getKey();
		NoteRequestTo request = new NoteRequestTo(key.getTweetId(),
												   note.getContent());
		request.setId(key.getId());
        return request;
	}
    
    private Note.NoteState performModeration(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Note.NoteState.DECLINED;
        }
        
        String lowerContent = content.toLowerCase();
        
        for (String stopWord : STOP_WORDS) {
            if (lowerContent.contains(stopWord)) {
                return Note.NoteState.DECLINED;
            }
        }
		
        if (content.length() < 2) {
            return Note.NoteState.DECLINED;
        }
        
        if (content.length() > 2048) {
            return Note.NoteState.DECLINED;
        }
        
        if (containsSpam(content)) {
            return Note.NoteState.DECLINED;
        }
        
        return Note.NoteState.APPROVED;
    }
    
    private boolean containsSpam(String content) {
        return content.matches(".(.)\\1{4,}.") ||
               content.matches(".[!?]{4,}.*");
    }
}