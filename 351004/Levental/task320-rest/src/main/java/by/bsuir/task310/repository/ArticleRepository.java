package by.bsuir.task310.repository;

import by.bsuir.task310.domain.Article;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ArticleRepository extends InMemoryCrudRepository<Article> {
    public Optional<Article> findByTitle(String title) {
        return storage.values().stream()
                .filter(a -> a.getTitle().equals(title))
                .findFirst();
    }

    public List<Article> findByCreatorId(Long creatorId) {
        return storage.values().stream()
                .filter(a -> a.getCreatorId().equals(creatorId))
                .toList();
    }
}
