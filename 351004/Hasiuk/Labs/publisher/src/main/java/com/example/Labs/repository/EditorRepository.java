package com.example.Labs.repository;
import com.example.Labs.entity.Editor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface EditorRepository extends BaseRepository<Editor, Long> {
    Optional<Editor> findByLogin(String login);
}