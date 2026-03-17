package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.model.entity.Writer;

public interface WriterRepository extends CrudRepository<Writer, Long> {

    boolean existsByLogin(String login);
}
