package com.labs.domain.repository;

import com.labs.domain.entity.Writer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WriterRepository extends BaseRepository<Writer, Long> {
    Optional<Writer> findByLogin(String login);
}

