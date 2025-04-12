package by.kopvzakone.distcomp.repositories;

import by.kopvzakone.distcomp.entities.Tweet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Repository
public interface TweetRepository extends Repo<Tweet> {
    @Query("SELECT COUNT(t) FROM Tweet t JOIN t.tags tag WHERE tag.id = :tagId")
    long countTweetsWithTag(@Param("tagId") Long tagId);
}
