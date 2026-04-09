package by.bsuir.distcomp.discussion.repository;

import by.bsuir.distcomp.discussion.domain.Reaction;
import by.bsuir.distcomp.discussion.domain.ReactionKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReactionRepository extends CassandraRepository<Reaction, ReactionKey> {
    // Поиск по tweet_id, который является частью составного ключа
    List<Reaction> findByKeyTweetId(Long tweetId);
}