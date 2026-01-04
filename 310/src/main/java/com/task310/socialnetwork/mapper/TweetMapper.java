package com.task310.socialnetwork.mapper;

import com.task310.socialnetwork.dto.request.TweetRequestTo;
import com.task310.socialnetwork.dto.response.TweetResponseTo;
import com.task310.socialnetwork.model.Tweet;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class TweetMapper {

    public Tweet toEntity(TweetRequestTo request) {
        if (request == null) {
            return null;
        }

        Tweet tweet = new Tweet();
        tweet.setUserId(request.getUserId());
        tweet.setTitle(request.getTitle());
        tweet.setContent(request.getContent());
        tweet.setLabelIds(request.getLabelIds());
        return tweet;
    }

    public TweetResponseTo toResponse(Tweet entity) {
        if (entity == null) {
            return null;
        }

        TweetResponseTo response = new TweetResponseTo();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());
        response.setLabelIds(entity.getLabelIds());
        return response;
    }
}