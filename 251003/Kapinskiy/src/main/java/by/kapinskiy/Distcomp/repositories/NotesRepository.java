package by.kapinskiy.Distcomp.repositories;

import by.kapinskiy.Distcomp.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Note, Long> {


}
