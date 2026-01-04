package com.socialnetwork.mapper;

import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import com.socialnetwork.model.Message;
import com.socialnetwork.model.Tweet;
import com.socialnetwork.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    @Autowired
    private TweetRepository tweetRepository;

    public Message toEntity(MessageRequestTo request) {
        if (request == null) {
            return null;
        }

        Message message = new Message();

        Tweet tweet = tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new IllegalArgumentException("Tweet not found with id: " + request.getTweetId()));
        message.setTweet(tweet);

        message.setContent(request.getContent());
        return message;
    }

    public MessageResponseTo toResponse(Message entity) {
        if (entity == null) {
            return null;
        }

        MessageResponseTo response = new MessageResponseTo();
        response.setId(entity.getId());
        response.setTweetId(entity.getTweet().getId());
        response.setContent(entity.getContent());
        return response;
    }
}