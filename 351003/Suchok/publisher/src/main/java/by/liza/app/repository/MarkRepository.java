package by.liza.app.repository;

import by.liza.app.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long>,
        JpaSpecificationExecutor<Mark> {

    Optional<Mark> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT m FROM Mark m JOIN m.articles a WHERE a.id = :articleId")
    List<Mark> findByArticleId(@Param("articleId") Long articleId);
}