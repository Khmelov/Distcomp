package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Long id);
}
