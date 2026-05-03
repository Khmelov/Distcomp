package by.bsuir.distcomp.client;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;

import java.util.List;

public interface DiscussionReactionClient {

    ReactionResponseTo create(ReactionRequestTo dto);

    ReactionResponseTo getById(Long id);

    List<ReactionResponseTo> getAll();

    List<ReactionResponseTo> getByTweetId(Long tweetId);

    ReactionResponseTo update(ReactionRequestTo dto);

    void deleteById(Long id);

    void deleteByTweetId(Long tweetId);
}
