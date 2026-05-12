package by.egorsosnovski.distcomp.repositories;

import by.egorsosnovski.distcomp.entities.Note;
import by.egorsosnovski.distcomp.entities.NoteKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
public interface NoteRepository extends CrudRepository<Note, NoteKey> {
    default Stream<Note> getAll(){
        return StreamSupport.stream(findAll().spliterator(), false);
    }
    default Optional<Note> get(NoteKey id){
        return findById(id);
    }
    default Optional<Note> create(Note input){
        return Optional.of(save(input));
    }
    default Optional<Note> update(Note input){
        return Optional.of(save(input));
    }
    default void delete(NoteKey id){
        if(findById(id).isPresent()) {
            deleteById(id);
        } else {
            throw new NoSuchElementException("No element with id " + id);
        }
    }
}
