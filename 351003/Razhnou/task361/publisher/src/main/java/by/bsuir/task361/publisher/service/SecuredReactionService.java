package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.dto.request.ReactionRequestTo;
import by.bsuir.task361.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task361.publisher.entity.Tweet;
import by.bsuir.task361.publisher.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecuredReactionService {
    private final ReactionKafkaGateway reactionKafkaGateway;
    private final TweetService tweetService;
    private final CurrentUserService currentUserService;

    public SecuredReactionService(
            ReactionKafkaGateway reactionKafkaGateway,
            TweetService tweetService,
            CurrentUserService currentUserService
    ) {
        this.reactionKafkaGateway = reactionKafkaGateway;
        this.tweetService = tweetService;
        this.currentUserService = currentUserService;
    }

    public ReactionResponseTo create(ReactionRequestTo request) {
        if (request.tweetId() != null && request.tweetId() > 0) {
            tweetService.getTweet(request.tweetId());
        }
        return reactionKafkaGateway.create(request);
    }

    public List<ReactionResponseTo> findAll() {
        return reactionKafkaGateway.findAll();
    }

    public ReactionResponseTo findById(Long id) {
        return reactionKafkaGateway.findById(id);
    }

    public ReactionResponseTo update(ReactionRequestTo request) {
        if (request.id() == null || request.id() <= 0) {
            return reactionKafkaGateway.update(request);
        }
        if (!currentUserService.isAdmin()) {
            ensureCurrentUserOwnsReaction(request.id());
        }
        return reactionKafkaGateway.update(request);
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            reactionKafkaGateway.delete(id);
            return;
        }
        if (!currentUserService.isAdmin()) {
            ensureCurrentUserOwnsReaction(id);
        }
        reactionKafkaGateway.delete(id);
    }

    private void ensureCurrentUserOwnsReaction(Long reactionId) {
        ReactionResponseTo reaction = reactionKafkaGateway.findById(reactionId);
        Tweet tweet = tweetService.getTweet(reaction.tweetId());
        if (!tweet.getUser().getLogin().equals(currentUserService.getCurrentLogin())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40301, "Access denied");
        }
    }
}
