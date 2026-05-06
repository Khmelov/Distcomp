package by.boukhvalova.distcomp.repositories;

import by.shaminko.distcomp.entities.Tweet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends Repo<Tweet> {
    @Query("SELECT COUNT(t) FROM Tweet t JOIN t.stickers sticker WHERE sticker.id = :stickerId")
    long countTweetsWithSticker(@Param("stickerId") Long stickerId);
}
