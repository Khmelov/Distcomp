package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.model.entity.Writer;

import java.util.Optional;

public interface WriterRepository extends CrudRepository<Writer, Long> {

    boolean existsByLogin(String login);

    Optional<Writer> findByLogin(String login);
}
