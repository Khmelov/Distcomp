package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.dto.request.TweetRequestTo;
import by.bsuir.task361.publisher.dto.response.TweetResponseTo;
import by.bsuir.task361.publisher.entity.Tweet;
import by.bsuir.task361.publisher.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecuredTweetService {
    private final TweetService tweetService;
    private final CurrentUserService currentUserService;

    public SecuredTweetService(TweetService tweetService, CurrentUserService currentUserService) {
        this.tweetService = tweetService;
        this.currentUserService = currentUserService;
    }

    public TweetResponseTo create(TweetRequestTo request) {
        if (currentUserService.isAdmin()) {
            return tweetService.create(request);
        }
        Long currentUserId = currentUserService.getCurrentUser().getId();
        return tweetService.create(new TweetRequestTo(
                request.id(),
                currentUserId,
                request.title(),
                request.content(),
                request.created(),
                request.modified(),
                request.tags(),
                request.tagIds()
        ));
    }

    public List<TweetResponseTo> findAll() {
        return tweetService.findAll();
    }

    public TweetResponseTo findById(Long id) {
        return tweetService.findById(id);
    }

    public TweetResponseTo update(TweetRequestTo request) {
        if (request.id() == null || request.id() <= 0) {
            return tweetService.update(request);
        }
        if (currentUserService.isAdmin()) {
            return tweetService.update(request);
        }
        Tweet tweet = tweetService.getTweet(request.id());
        if (!tweet.getUser().getLogin().equals(currentUserService.getCurrentLogin())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40301, "Access denied");
        }
        Long currentUserId = currentUserService.getCurrentUser().getId();
        return tweetService.update(new TweetRequestTo(
                request.id(),
                currentUserId,
                request.title(),
                request.content(),
                request.created(),
                request.modified(),
                request.tags(),
                request.tagIds()
        ));
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            tweetService.delete(id);
            return;
        }
        if (currentUserService.isAdmin()) {
            tweetService.delete(id);
            return;
        }
        Tweet tweet = tweetService.getTweet(id);
        if (!tweet.getUser().getLogin().equals(currentUserService.getCurrentLogin())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40301, "Access denied");
        }
        tweetService.delete(id);
    }
}
