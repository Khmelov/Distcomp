package by.molchan.Publisher.repositories;

import by.molchan.Publisher.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ArticlesRepository extends JpaRepository<Article, Long> {
    boolean existsByTitle(String title);
}
