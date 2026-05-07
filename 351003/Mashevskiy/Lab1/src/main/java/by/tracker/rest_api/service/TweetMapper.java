package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.TweetRequestTo;
import by.tracker.rest_api.dto.TweetResponseTo;
import by.tracker.rest_api.model.Tweet;
import org.springframework.stereotype.Component;

@Component
public class TweetMapper {

    public Tweet toEntity(TweetRequestTo request) {
        Tweet tweet = new Tweet();
        tweet.setId(request.getId());
        tweet.setCreatorId(request.getCreatorId());  // ← должно быть Long
        tweet.setTitle(request.getTitle());
        tweet.setContent(request.getContent());
        return tweet;
    }

    public TweetResponseTo toResponse(Tweet entity) {
        TweetResponseTo response = new TweetResponseTo();
        response.setId(entity.getId());
        response.setCreatorId(entity.getCreatorId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());
        return response;
    }
}