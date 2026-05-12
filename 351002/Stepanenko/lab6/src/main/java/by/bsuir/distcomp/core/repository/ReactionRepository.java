package by.bsuir.distcomp.core.repository;
import by.bsuir.distcomp.core.domain.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findByTweetId(Long tweetId);
}