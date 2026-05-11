package com.adashkevich.jpa.lab.repository;

import com.adashkevich.jpa.lab.model.Editor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EditorRepository extends JpaRepository<Editor, Long> {
    boolean existsByLoginIgnoreCase(String login);
    boolean existsByLoginIgnoreCaseAndIdNot(String login, Long id);
    Optional<Editor> findByLoginIgnoreCase(String login);
}
