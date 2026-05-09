package com.lizaveta.notebook.repository.jpa;

import com.lizaveta.notebook.repository.entity.WriterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WriterJpaRepository extends JpaRepository<WriterEntity, Long> {

    boolean existsByLogin(String login);

    Optional<WriterEntity> findByLogin(String login);
}
