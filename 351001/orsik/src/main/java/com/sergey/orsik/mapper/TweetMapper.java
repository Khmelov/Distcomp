package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.entity.Tweet;
import org.springframework.stereotype.Component;

@Component
public class TweetMapper {

    public Tweet toEntity(TweetRequestTo request) {
        if (request == null) {
            return null;
        }
        Tweet tweet = new Tweet();
        tweet.setId(request.getId());
        tweet.setCreatorId(request.getCreatorId());
        tweet.setTitle(request.getTitle());
        tweet.setContent(request.getContent());
        tweet.setCreated(request.getCreated());
        tweet.setModified(request.getModified());
        return tweet;
    }

    public TweetResponseTo toResponse(Tweet entity) {
        if (entity == null) {
            return null;
        }
        return new TweetResponseTo(
                entity.getId(),
                entity.getCreatorId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreated(),
                entity.getModified()
        );
    }
}
