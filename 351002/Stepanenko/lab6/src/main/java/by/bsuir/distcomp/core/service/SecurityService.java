package by.bsuir.distcomp.core.service;

import by.bsuir.distcomp.core.repository.AuthorRepository;
import by.bsuir.distcomp.core.repository.TweetRepository;
import by.bsuir.distcomp.core.repository.ReactionRepository;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {
    private final TweetRepository tweetRepository;
    private final ReactionRepository reactionRepository;
    private final AuthorRepository authorRepository;

    public SecurityService(TweetRepository tweetRepository, ReactionRepository reactionRepository, AuthorRepository authorRepository) {
        this.tweetRepository = tweetRepository;
        this.reactionRepository = reactionRepository;
        this.authorRepository = authorRepository;
    }

    public boolean isTweetOwner(Long tweetId, String login) {
        return tweetRepository.findById(tweetId)
                .map(t -> t.getAuthor().getLogin().equals(login))
                .orElse(false);
    }

    public boolean isAuthorOwner(Long authorId, String login) {
        return authorRepository.findById(authorId)
                .map(a -> a.getLogin().equals(login))
                .orElse(false);
    }
}