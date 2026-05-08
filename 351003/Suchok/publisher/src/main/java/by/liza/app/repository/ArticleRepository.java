package by.liza.app.repository;

import by.liza.app.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>,
        JpaSpecificationExecutor<Article> {

    Optional<Article> findByTitle(String title);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

    List<Article> findByWriterId(Long writerId);

    @Query("SELECT a FROM Article a JOIN a.marks m WHERE m.id = :markId")
    List<Article> findByMarkId(@Param("markId") Long markId);
}