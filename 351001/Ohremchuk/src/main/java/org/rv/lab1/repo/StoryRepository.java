package org.rv.lab1.repo;

import org.rv.lab1.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findAllByEditorLogin(String login);

    boolean existsByTitle(String title);

    // avoid LazyInitializationException in mappers (editor + markers)
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"editor", "markers"})
    Optional<Story> findWithRelationsById(Long id);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"editor", "markers"})
    List<Story> findAllWithRelationsBy();
}

