package com.jpa.service;

import com.jpa.dto.request.NoteRequestTo;
import com.jpa.dto.response.NoteResponseTo;
import com.jpa.entity.Note;
import com.jpa.entity.Tweet;
import com.jpa.mapper.NoteMapper;
import com.jpa.repository.NoteRepository;
import com.jpa.repository.TweetRepository;
import com.jpa.exception.NotFoundException;
import com.jpa.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@Validated
public class NoteService {
	
	@Autowired
	private final NoteRepository noteRepository;
	
	@Autowired
	private final TweetRepository tweetRepository;
	
	@Autowired
	private final NoteMapper noteMapper;
	
	public NoteService(NoteRepository noteRepository,
					   TweetRepository tweetRepository,
					   NoteMapper noteMapper) {
		this.noteRepository = noteRepository;
		this.tweetRepository = tweetRepository;
		this.noteMapper = noteMapper;
	}
	
	@Transactional
	public NoteResponseTo create(@Valid NoteRequestTo request) {
		validateNoteRequest(request);
		
		Tweet tweet = tweetRepository.findById(request.getTweetId())
			.orElseThrow(() -> new NotFoundException("Tweet not found: " + request.getTweetId()));
		
		Note note = noteMapper.toEntity(request);
		note.setTweet(tweet);
		Note saved = noteRepository.save(note);
		return noteMapper.toResponse(saved);
	}
	
	@Transactional
	public NoteResponseTo findById(Long id) {
		Note note = noteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Note not found: " + id));
		return noteMapper.toResponse(note);
	}
	
	@Transactional
	public List<NoteResponseTo> findAll() {
		return noteRepository.findAll().stream()
			.map(noteMapper::toResponse)
			.toList();
	}
	
	@Transactional
	public NoteResponseTo update(Long id, @Valid NoteRequestTo request) {
		validateNoteRequest(request);
		
		Note existing = noteRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Note not found: " + id));
		
		Tweet tweet = tweetRepository.findById(request.getTweetId())
			.orElseThrow(() -> new NotFoundException("Tweet not found: " + request.getTweetId()));
		
		existing.setTweet(tweet);
		noteMapper.updateEntity(request, existing);
		Note updated = noteRepository.save(existing);
		return noteMapper.toResponse(updated);
	}
	
	@Transactional
	public void delete(Long id) {
		if (!noteRepository.existsById(id)) {
			throw new NotFoundException("Note not found: " + id);
		}
		noteRepository.deleteById(id);
	}
	
	@Transactional
	public List<NoteResponseTo> findByTweetId(Long TweetId) {
		if (!tweetRepository.existsById(TweetId)) {
			throw new NotFoundException("Tweet not found: " + TweetId);
		}
		
		return noteRepository.findByTweetId(TweetId).stream()
			.map(noteMapper::toResponse)
			.toList();
	}
	
	private void validateNoteRequest(NoteRequestTo request) {
		if (request.getTweetId() == null) {
			throw new ValidationException("Tweet ID is required");
		}
		if (request.getContent() == null || request.getContent().length() < 2 || request.getContent().length() > 2048) {
			throw new ValidationException("Content must be 2-2048 characters");
		}
	}
}