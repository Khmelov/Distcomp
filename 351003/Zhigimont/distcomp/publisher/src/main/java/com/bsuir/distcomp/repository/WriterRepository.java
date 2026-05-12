package com.bsuir.distcomp.repository;

import com.bsuir.distcomp.entity.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long> {
    Optional<Writer> findByLogin(String login);
    boolean existsByLogin(String login);
}
