package com.lizaveta.notebook.repository.jpa;

import com.lizaveta.notebook.repository.entity.WriterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriterJpaRepository extends JpaRepository<WriterEntity, Long> {

    boolean existsByLogin(String login);
}
