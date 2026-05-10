package com.sergey.orsik.service.impl;

import com.sergey.orsik.entity.Label;
import com.sergey.orsik.entity.Tweet;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.repository.LabelRepository;
import com.sergey.orsik.repository.TweetRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DB-only part of tweet deletion so the outer flow can call discussion (HTTP) without holding a JPA transaction.
 */
@Component
public class TweetDeletionHelper {

    private final TweetRepository tweetRepository;
    private final LabelRepository labelRepository;

    TweetDeletionHelper(TweetRepository tweetRepository, LabelRepository labelRepository) {
        this.tweetRepository = tweetRepository;
        this.labelRepository = labelRepository;
    }

    @Transactional
    public void deleteTweetAndOrphanLabels(Long id) {
        Tweet tweet = tweetRepository.findByIdWithLabels(id)
                .orElseThrow(() -> new EntityNotFoundException("Tweet", id));
        Set<Long> labelIds = tweet.getLabels().stream().map(Label::getId).collect(Collectors.toCollection(HashSet::new));
        tweetRepository.delete(tweet);
        tweetRepository.flush();
        for (Long labelId : labelIds) {
            labelRepository.findById(labelId).ifPresent(label -> {
                if (label.getTweets().isEmpty()) {
                    labelRepository.delete(label);
                }
            });
        }
    }
}
