package com.example.demo.repository;

import com.example.demo.models.Writer;

public interface WriterRepository extends BaseJpaRepository<Writer> {
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, Long id);
}
