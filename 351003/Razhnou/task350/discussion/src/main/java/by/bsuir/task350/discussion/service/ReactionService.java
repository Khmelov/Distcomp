package by.bsuir.task350.discussion.service;

import by.bsuir.task350.discussion.dto.ReactionState;
import by.bsuir.task350.discussion.dto.request.ReactionRequestTo;
import by.bsuir.task350.discussion.dto.response.ReactionResponseTo;
import by.bsuir.task350.discussion.entity.Reaction;
import by.bsuir.task350.discussion.exception.BadRequestException;
import by.bsuir.task350.discussion.exception.ConflictException;
import by.bsuir.task350.discussion.exception.NotFoundException;
import by.bsuir.task350.discussion.mapper.ReactionMapper;
import by.bsuir.task350.discussion.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReactionService {
    private final AtomicLong sequence = new AtomicLong(System.currentTimeMillis());
    private final ReactionRepository reactionRepository;
    private final ReactionModerationService reactionModerationService;

    public ReactionService(ReactionRepository reactionRepository, ReactionModerationService reactionModerationService) {
        this.reactionRepository = reactionRepository;
        this.reactionModerationService = reactionModerationService;
    }

    public ReactionResponseTo createFromRest(ReactionRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Reaction id must be null on create", 3);
        }
        return createInternal(sequence.incrementAndGet(), request);
    }

    public ReactionResponseTo createFromTransport(ReactionRequestTo request) {
        Long id = request.id() == null ? sequence.incrementAndGet() : request.id();
        return createInternal(id, request);
    }

    public List<ReactionResponseTo> findAll() {
        return reactionRepository.findAll().stream()
                .map(ReactionMapper::toResponse)
                .toList();
    }

    public ReactionResponseTo findById(Long id) {
        validateId(id, "Reaction id");
        return ReactionMapper.toResponse(getReaction(id));
    }

    public ReactionResponseTo update(ReactionRequestTo request) {
        validateId(request.id(), "Reaction id");
        validateTweetId(request.tweetId());
        validateContent(request.content());

        Reaction reaction = getReaction(request.id());
        ReactionState state = reactionModerationService.moderate(request.content());
        ReactionMapper.updateEntity(reaction, request, state);
        return ReactionMapper.toResponse(reactionRepository.save(reaction));
    }

    public void delete(Long id) {
        validateId(id, "Reaction id");
        reactionRepository.delete(getReaction(id));
    }

    private ReactionResponseTo createInternal(Long id, ReactionRequestTo request) {
        validateTweetId(request.tweetId());
        validateContent(request.content());
        if (reactionRepository.existsById(id)) {
            throw new ConflictException("Reaction id already exists", 2);
        }

        ReactionState state = reactionModerationService.moderate(request.content());
        Reaction reaction = ReactionMapper.toEntity(id, request, state);
        return ReactionMapper.toResponse(reactionRepository.save(reaction));
    }

    private Reaction getReaction(Long id) {
        return reactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reaction not found", 1));
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateTweetId(Long tweetId) {
        if (tweetId == null || tweetId <= 0) {
            throw new BadRequestException("Tweet id must be greater than 0", 2);
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException("Reaction content must not be blank", 4);
        }
        int length = content.trim().length();
        if (length < 2 || length > 2048) {
            throw new BadRequestException("Reaction content length must be between 2 and 2048", 5);
        }
    }
}
