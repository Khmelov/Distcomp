package by.shaminko.distcomp.repositories;

import by.shaminko.distcomp.entities.Tweet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends Repo<Tweet> {
    @Query("SELECT COUNT(t) FROM Tweet t JOIN t.tags tag WHERE tag.id = :tagId")
    long countTweetsWithTag(@Param("tagId") Long tagId);
}
