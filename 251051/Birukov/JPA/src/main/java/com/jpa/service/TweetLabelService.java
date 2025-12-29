package com.jpa.service;

import com.jpa.dto.request.TweetLabelRequestTo;
import com.jpa.dto.response.TweetLabelResponseTo;
import com.jpa.entity.TweetLabel;
import com.jpa.entity.Tweet;
import com.jpa.entity.Label;
import com.jpa.mapper.TweetLabelMapper;
import com.jpa.repository.TweetLabelRepository;
import com.jpa.repository.TweetRepository;
import com.jpa.repository.LabelRepository;
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
public class TweetLabelService {
	
	@Autowired
	private final TweetLabelRepository tweetLabelRepository;
	
	@Autowired
	private final TweetRepository tweetRepository;
	
	@Autowired
	private final LabelRepository labelRepository;
	
	@Autowired
	private final TweetLabelMapper tweetLabelMapper;
	
	public TweetLabelService(TweetLabelRepository tweetLabelRepository,
					   TweetRepository tweetRepository,
					   LabelRepository labelRepository,
					   TweetLabelMapper tweetLabelMapper) {
		this.tweetLabelRepository = tweetLabelRepository;
		this.tweetRepository = tweetRepository;
		this.labelRepository = labelRepository;
		this.tweetLabelMapper = tweetLabelMapper;
	}
	
	@Transactional
	public TweetLabelResponseTo create(@Valid TweetLabelRequestTo request) {
		validateTweetLabelRequest(request);
		
		Tweet tweet = tweetRepository.findById(request.getTweetId())
			.orElseThrow(() -> new NotFoundException("Tweet not found: " + request.getTweetId()));
		
		Label label = labelRepository.findById(request.getLabelId())
			.orElseThrow(() -> new NotFoundException("Label not found: " + request.getLabelId()));
		
		TweetLabel tweetLabel = tweetLabelMapper.toEntity(request);
		tweetLabel.setTweet(tweet);
		tweetLabel.setLabel(label);
		TweetLabel saved = tweetLabelRepository.save(tweetLabel);
		return tweetLabelMapper.toResponse(saved);
	}
	
	@Transactional
	public TweetLabelResponseTo findById(Long id) {
		TweetLabel tweetLabel = tweetLabelRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("TweetLabel not found: " + id));
		return tweetLabelMapper.toResponse(tweetLabel);
	}
	
	@Transactional
	public List<TweetLabelResponseTo> findAll() {
		return tweetLabelRepository.findAll().stream()
			.map(tweetLabelMapper::toResponse)
			.toList();
	}
	
	@Transactional
	public TweetLabelResponseTo update(Long id, @Valid TweetLabelRequestTo request) {
		validateTweetLabelRequest(request);
		
		TweetLabel existing = tweetLabelRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("TweetLabel not found: " + id));
		
		Tweet tweet = tweetRepository.findById(request.getTweetId())
			.orElseThrow(() -> new NotFoundException("Tweet not found: " + request.getTweetId()));
		
		Label label = labelRepository.findById(request.getLabelId())
			.orElseThrow(() -> new NotFoundException("Label not found: " + request.getLabelId()));
		
		existing.setTweet(tweet);
		existing.setLabel(label);
		tweetLabelMapper.updateEntity(request, existing);
		TweetLabel updated = tweetLabelRepository.save(existing);
		return tweetLabelMapper.toResponse(updated);
	}
	
	@Transactional
	public void delete(Long id) {
		if (!tweetLabelRepository.existsById(id)) {
			throw new NotFoundException("TweetLabel not found: " + id);
		}
		tweetLabelRepository.deleteById(id);
	}
	
	@Transactional
	public List<TweetLabelResponseTo> findByTweetId(Long tweetId) {
		if (!tweetRepository.existsById(tweetId)) {
			throw new NotFoundException("Tweet not found: " + tweetId);
		}
		
		return tweetLabelRepository.findByTweetId(tweetId).stream()
			.map(tweetLabelMapper::toResponse)
			.toList();
	}
	
	@Transactional
	public List<TweetLabelResponseTo> findByLabelId(Long labelId) {
		if (!labelRepository.existsById(labelId)) {
			throw new NotFoundException("Label not found: " + labelId);
		}
		
		return tweetLabelRepository.findByLabelId(labelId).stream()
			.map(tweetLabelMapper::toResponse)
			.toList();
	}
	
	private void validateTweetLabelRequest(TweetLabelRequestTo request) {
		if (request.getTweetId() == null) {
			throw new ValidationException("Tweet ID is required");
		}
		
		if (request.getLabelId() == null) {
			throw new ValidationException("Label ID is required");
		}
	}
}