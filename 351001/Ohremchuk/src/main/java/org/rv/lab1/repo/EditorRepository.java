package org.rv.lab1.repo;

import org.rv.lab1.domain.Editor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorRepository extends JpaRepository<Editor, Long> {
    boolean existsByLogin(String login);

    java.util.Optional<Editor> findByLogin(String login);
}

