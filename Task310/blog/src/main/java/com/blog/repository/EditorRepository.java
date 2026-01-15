package com.blog.repository;

import com.blog.model.Editor;
import java.util.Optional;

public interface EditorRepository extends CrudRepository<Editor, Long> {
    Optional<Editor> findByLogin(String login);
}