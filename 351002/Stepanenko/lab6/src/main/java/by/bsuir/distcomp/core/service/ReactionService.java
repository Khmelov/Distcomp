package by.bsuir.distcomp.core.service;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.core.domain.Reaction;
import by.bsuir.distcomp.core.domain.Tweet;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.core.mapper.ReactionMapper;
import by.bsuir.distcomp.core.repository.ReactionRepository;
import by.bsuir.distcomp.core.repository.TweetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final TweetRepository tweetRepository;
    private final ReactionMapper reactionMapper;

    public ReactionService(ReactionRepository reactionRepository, TweetRepository tweetRepository, ReactionMapper reactionMapper) {
        this.reactionRepository = reactionRepository;
        this.tweetRepository = tweetRepository;
        this.reactionMapper = reactionMapper;
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        Tweet tweet = tweetRepository.findById(dto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40416));
        Reaction reaction = reactionMapper.toEntity(dto);
        reaction.setTweet(tweet);
        return reactionMapper.toResponseDto(reactionRepository.save(reaction));
    }

    public ReactionResponseTo getById(Long id) {
        return reactionRepository.findById(id)
                .map(reactionMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found", 40417));
    }

    public ReactionResponseTo update(ReactionRequestTo dto) {
        Reaction existing = reactionRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found", 40418));
        Tweet tweet = tweetRepository.findById(dto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40419));

        existing.setContent(dto.getContent());
        existing.setTweet(tweet);
        return reactionMapper.toResponseDto(reactionRepository.save(existing));
    }

    public void deleteById(Long id) {
        if (!reactionRepository.existsById(id)) throw new ResourceNotFoundException("Reaction not found", 40420);
        reactionRepository.deleteById(id);
    }

    public List<ReactionResponseTo> getAll() {
        return reactionRepository.findAll().stream().map(reactionMapper::toResponseDto).collect(Collectors.toList());
    }
}