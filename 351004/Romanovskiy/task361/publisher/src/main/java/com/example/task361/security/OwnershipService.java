package com.example.task361.security;

import com.example.task361.domain.entity.Author;
import com.example.task361.domain.entity.Reaction;
import com.example.task361.domain.entity.Tweet;
import com.example.task361.repository.AuthorRepository;
import com.example.task361.repository.ReactionRepository;
import com.example.task361.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("ownershipService")
@RequiredArgsConstructor
public class OwnershipService {
    private final AuthorRepository authorRepository;
    private final TweetRepository tweetRepository;
    private final ReactionRepository reactionRepository;

    public boolean isSelf(Long authorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return false;
        Author me = authorRepository.findByLogin(auth.getName()).orElse(null);
        return me != null && me.getId() != null && me.getId().equals(authorId);
    }

    public boolean isCurrentAuthorId(Long authorId) {
        return isSelf(authorId);
    }

    public boolean isTweetOwner(Long tweetId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return false;
        Author me = authorRepository.findByLogin(auth.getName()).orElse(null);
        if (me == null) return false;
        Tweet tweet = tweetRepository.findById(tweetId).orElse(null);
        return tweet != null && me.getId() != null && me.getId().equals(tweet.getAuthorId());
    }

    public boolean isTweetOwnerByTweetId(Long tweetId) {
        return isTweetOwner(tweetId);
    }

    public boolean isReactionOnOwnedTweet(Long reactionId) {
        Reaction reaction = reactionRepository.findById(reactionId).orElse(null);
        if (reaction == null) return false;
        return isTweetOwner(reaction.getTweetId());
    }
}
