package com.publisher.service;

import com.publisher.dto.request.TweetRequestTo;
import com.publisher.dto.response.TweetResponseTo;
import com.publisher.entity.Tweet;
import com.publisher.entity.Writer;
import com.publisher.mapper.TweetMapper;
import com.publisher.repository.TweetRepository;
import com.publisher.repository.WriterRepository;
import com.publisher.exception.NotFoundException;
import com.publisher.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@Validated
public class TweetService {
	
	@Autowired
	private final TweetRepository tweetRepository;
	
	@Autowired
	private final WriterRepository writerRepository;
	
	@Autowired
	private final TweetMapper tweetMapper;
	
	public TweetService(TweetRepository tweetRepository,
					   WriterRepository writerRepository,
					   TweetMapper tweetMapper) {
		this.tweetRepository = tweetRepository;
		this.writerRepository = writerRepository;
		this.tweetMapper = tweetMapper;
	}
	
	public TweetResponseTo create(@Valid TweetRequestTo request) {
		validateTweetRequest(request);
		
		Writer writer = writerRepository.findById(request.getWriterId())
			.orElseThrow(() -> new NotFoundException("Writer not found: " + request.getWriterId()));
		
		if (tweetRepository.existsByTitle(request.getTitle())) {
			throw new ValidationException("Tweet title already exists: " + request.getTitle());
		}
		
		Tweet tweet = tweetMapper.toEntity(request);
		tweet.setWriter(writer);
		Tweet saved = tweetRepository.save(tweet);
		return tweetMapper.toResponse(saved);
	}
	
	public TweetResponseTo findById(Long id) {
		Tweet tweet = tweetRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
		return tweetMapper.toResponse(tweet);
	}
	
	public List<TweetResponseTo> findAll() {
		return tweetRepository.findAll().stream()
			.map(tweetMapper::toResponse)
			.toList();
	}
	
	public TweetResponseTo update(Long id, @Valid TweetRequestTo request) {
		validateTweetRequest(request);
		
		Tweet existing = tweetRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
		
		Writer writer = writerRepository.findById(request.getWriterId())
			.orElseThrow(() -> new NotFoundException("Writer not found: " + request.getWriterId()));
		
		if (!existing.getTitle().equals(request.getTitle()) && 
			tweetRepository.existsByTitle(request.getTitle())) {
			throw new ValidationException("Tweet title already exists: " + request.getTitle());
		}
		
		existing.setWriter(writer);
		tweetMapper.updateEntity(request, existing);
		Tweet updated = tweetRepository.save(existing);
		return tweetMapper.toResponse(updated);
	}
	
	public void delete(Long id) {
		if (!tweetRepository.existsById(id)) {
			throw new NotFoundException("Tweet not found: " + id);
		}
		tweetRepository.deleteById(id);
	}
	
	public List<TweetResponseTo> findByWriterId(Long writerId) {
		if (!writerRepository.existsById(writerId)) {
			throw new NotFoundException("Writer not found: " + writerId);
		}
		
		return tweetRepository.findByWriterId(writerId).stream()
			.map(tweetMapper::toResponse)
			.toList();
	}
	
	private void validateTweetRequest(TweetRequestTo request) {
		if (request.getWriterId() == null) {
			throw new ValidationException("Writer ID is required");
		}
		if (request.getTitle() == null || request.getTitle().length() < 2 || request.getTitle().length() > 64) {
			throw new ValidationException("Title must be 2-64 characters");
		}
		if (request.getContent() == null || request.getContent().length() < 4 || request.getContent().length() > 2048) {
			throw new ValidationException("Content must be 4-2048 characters");
		}
	}
}