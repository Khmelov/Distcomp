package by.bsuir.distcomp.discussion.repository;

import by.bsuir.distcomp.discussion.domain.Reaction;
import by.bsuir.distcomp.discussion.domain.ReactionKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface ReactionRepository extends CassandraRepository<Reaction, ReactionKey> {

    @Query("SELECT * FROM tbl_reaction WHERE tweet_id = ?0")
    List<Reaction> findAllByTweetId(Long tweetId);
}
