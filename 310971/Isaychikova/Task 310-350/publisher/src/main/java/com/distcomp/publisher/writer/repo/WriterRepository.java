package com.distcomp.publisher.writer.repo;

import com.distcomp.publisher.writer.domain.Writer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface WriterRepository extends JpaRepository<Writer, Long> {
    boolean existsByLogin(String login);

    @EntityGraph(attributePaths = {"articles", "articles.stickers"})
    Optional<Writer> findWithArticlesById(Long id);
}
