package by.bsuir.task340.publisher.repository;

import by.bsuir.task340.publisher.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TweetRepository extends JpaRepository<Tweet, Long>, JpaSpecificationExecutor<Tweet> {
    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);
}
