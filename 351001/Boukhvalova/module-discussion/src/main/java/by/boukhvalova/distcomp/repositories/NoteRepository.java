package by.boukhvalova.distcomp.repositories;

import by.boukhvalova.distcomp.entities.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {
    default Stream<Note> getAll(){
        return StreamSupport.stream(findAll().spliterator(), false);
    }

    @Override
    List<Note> findAll();

    default Optional<Note> get(Long id){
        return findById(id);
    }

    default Optional<Note> create(Note input){
        return Optional.of(save(input));
    }

    default Optional<Note> update(Note input){
        return Optional.of(save(input));
    }

    default void delete(Long id){
        if(findById(id).isPresent()) {
            deleteById(id);
        } else {
            throw new NoSuchElementException("No element with id " + id);
        }
    }
}
