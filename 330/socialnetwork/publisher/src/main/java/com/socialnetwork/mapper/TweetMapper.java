package com.socialnetwork.mapper;

import com.socialnetwork.dto.request.TweetRequestTo;
import com.socialnetwork.dto.response.TweetResponseTo;
import com.socialnetwork.model.Label;
import com.socialnetwork.model.Tweet;
import com.socialnetwork.model.User;
import com.socialnetwork.repository.UserRepository;
import com.socialnetwork.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TweetMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    public Tweet toEntity(TweetRequestTo request) {
        if (request == null) {
            return null;
        }

        Tweet tweet = new Tweet();

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
        tweet.setUser(user);

        tweet.setTitle(request.getTitle());
        tweet.setContent(request.getContent());

        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : request.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));
                labels.add(label);
            }
            tweet.setLabels(labels);
        }

        return tweet;
    }

    public TweetResponseTo toResponse(Tweet entity) {
        if (entity == null) {
            return null;
        }

        TweetResponseTo response = new TweetResponseTo();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setCreated(entity.getCreated());
        response.setModified(entity.getModified());

        if (entity.getLabels() != null) {
            Set<Long> labelIds = new HashSet<>();
            for (Label label : entity.getLabels()) {
                labelIds.add(label.getId());
            }
            response.setLabelIds(labelIds);
        }

        return response;
    }
}