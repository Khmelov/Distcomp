package by.bsuir.distcomp.core.service;

import by.bsuir.distcomp.client.DiscussionReactionClient;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.dto.reaction.ReactionState;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.core.repository.TweetRepository;
import by.bsuir.distcomp.publisher.service.ReactionIdGenerator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReactionService {
    private final DiscussionReactionClient client;
    private final TweetRepository tweetRepository;
    private final ReactionIdGenerator idGenerator;

    public ReactionService(DiscussionReactionClient client, TweetRepository tweetRepository, ReactionIdGenerator idGenerator) {
        this.client = client;
        this.tweetRepository = tweetRepository;
        this.idGenerator = idGenerator;
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        tweetRepository.findById(dto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40416));
        dto.setId(idGenerator.nextId());
        ReactionResponseTo response = client.create(dto);
        response.setState(ReactionState.PENDING);
        return response;
    }

    @Cacheable(value = "reactions", key = "#id")
    public ReactionResponseTo getById(Long id) { return client.getById(id); }

    public List<ReactionResponseTo> getByTweetId(Long tweetId) { return client.getByTweetId(tweetId); }

    public List<ReactionResponseTo> getAll() { return client.getAll(); }

    @CacheEvict(value = "reactions", key = "#id")
    public void deleteById(Long id) { client.deleteById(id); }

    @CacheEvict(value = "reactions", key = "#dto.id")
    public ReactionResponseTo update(ReactionRequestTo dto) {
        tweetRepository.findById(dto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40419));
        return client.update(dto);
    }
}