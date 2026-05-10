package by.bsuir.distcomp.core.service;

import by.bsuir.distcomp.client.DiscussionReactionClient;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.core.repository.TweetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReactionService {

    private final DiscussionReactionClient discussionReactionClient;
    private final TweetRepository tweetRepository;

    public ReactionService(DiscussionReactionClient discussionReactionClient,
                           TweetRepository tweetRepository) {
        this.discussionReactionClient = discussionReactionClient;
        this.tweetRepository = tweetRepository;
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        tweetRepository.findById(dto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40416));
        return discussionReactionClient.create(dto);
    }

    public ReactionResponseTo getById(Long id) {
        return discussionReactionClient.getById(id);
    }

    public List<ReactionResponseTo> getByTweetId(Long tweetId) {
        return discussionReactionClient.getByTweetId(tweetId);
    }

    public ReactionResponseTo update(ReactionRequestTo dto) {
        tweetRepository.findById(dto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40419));
        return discussionReactionClient.update(dto);
    }

    public void deleteById(Long id) {
        discussionReactionClient.deleteById(id);
    }

    public List<ReactionResponseTo> getAll() {
        return discussionReactionClient.getAll();
    }
}
