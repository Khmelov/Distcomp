package by.tracker.rest_api.repository;

import by.tracker.rest_api.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    boolean existsByTitle(String title);
}