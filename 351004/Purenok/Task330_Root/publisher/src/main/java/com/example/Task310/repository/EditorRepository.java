package com.example.Task310.repository;

import com.example.Task310.bean.Editor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorRepository extends JpaRepository<Editor, Long> {
    // Используется в EditorService для проверки дубликатов
    boolean existsByLogin(String login);
}



