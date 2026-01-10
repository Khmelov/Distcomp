package com.blog.repository;

import com.blog.entity.Writer;

import java.util.Optional;

public interface WriterRepository extends BaseJpaRepository<Writer, Long> {
    Optional<Writer> findByLogin(String login);
}