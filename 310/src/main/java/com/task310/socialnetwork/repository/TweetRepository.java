package com.task310.socialnetwork.repository;

import com.task310.socialnetwork.model.Tweet;
import java.util.List;

public interface TweetRepository extends CrudRepository<Tweet, Long> {
    List<Tweet> findByUserId(Long userId);
    List<Tweet> findByLabelIdsContaining(Long labelId);
}