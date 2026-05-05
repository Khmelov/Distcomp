package by.distcomp.app.repository;

import by.distcomp.app.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitle(String login);
}
