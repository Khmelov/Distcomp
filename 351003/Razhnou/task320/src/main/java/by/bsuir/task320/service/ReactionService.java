package by.bsuir.task320.service;

import by.bsuir.task320.dto.request.ReactionRequestTo;
import by.bsuir.task320.dto.response.ReactionResponseTo;
import by.bsuir.task320.entity.Reaction;
import by.bsuir.task320.entity.Tweet;
import by.bsuir.task320.exception.BadRequestException;
import by.bsuir.task320.exception.NotFoundException;
import by.bsuir.task320.mapper.ReactionMapper;
import by.bsuir.task320.repository.ReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final TweetService tweetService;

    public ReactionService(ReactionRepository reactionRepository, TweetService tweetService) {
        this.reactionRepository = reactionRepository;
        this.tweetService = tweetService;
    }

    @Transactional
    public ReactionResponseTo create(ReactionRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Reaction id must be null on create", 3);
        }
        validateId(request.tweetId(), "Tweet id");
        validateContent(request.content());

        Tweet tweet = tweetService.getTweet(request.tweetId());
        Reaction reaction = ReactionMapper.toEntity(tweet, request.content());
        return ReactionMapper.toResponse(reactionRepository.save(reaction));
    }

    @Transactional(readOnly = true)
    public List<ReactionResponseTo> findAll() {
        return reactionRepository.findAll().stream()
                .map(ReactionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReactionResponseTo findById(Long id) {
        validateId(id, "Reaction id");
        return ReactionMapper.toResponse(getReaction(id));
    }

    @Transactional
    public ReactionResponseTo update(ReactionRequestTo request) {
        validateId(request.id(), "Reaction id");
        validateId(request.tweetId(), "Tweet id");
        validateContent(request.content());

        Reaction reaction = getReaction(request.id());
        Tweet tweet = tweetService.getTweet(request.tweetId());
        ReactionMapper.updateEntity(reaction, tweet, request.content());
        return ReactionMapper.toResponse(reactionRepository.save(reaction));
    }

    @Transactional
    public void delete(Long id) {
        validateId(id, "Reaction id");
        Reaction reaction = getReaction(id);
        reactionRepository.delete(reaction);
    }

    public Reaction getReaction(Long id) {
        return reactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reaction not found", 3));
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException("Reaction content must not be blank", 2);
        }
        int length = content.trim().length();
        if (length < 2 || length > 2048) {
            throw new BadRequestException("Reaction content length must be between 2 and 2048", 4);
        }
    }
}
