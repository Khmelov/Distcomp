package com.example.discussion.repository;

import com.example.discussion.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

//    Optional<Note> findByKeyCountryAndKeyArticleIdAndKeyId(String country, Long articleId, Long id);

    //void deleteByKeyCountryAndKeyArticleIdAndKeyId(String country, Long articleId, Long id);
}
