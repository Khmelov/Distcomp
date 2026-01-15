package by.bsuir.entitiesapp.repository;

import org.springframework.stereotype.Repository;

import by.bsuir.entitiesapp.entity.Note;

@Repository
public interface NoteRepository extends BaseRepository<Note, Long> {
}
