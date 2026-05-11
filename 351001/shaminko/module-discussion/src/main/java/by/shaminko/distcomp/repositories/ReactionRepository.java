package by.shaminko.distcomp.repositories;

import by.shaminko.distcomp.entities.Reaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
public interface ReactionRepository extends CrudRepository<Reaction, Long> {
    default Stream<Reaction> getAll(){
        return StreamSupport.stream(findAll().spliterator(), false);
    }

    @Override
    List<Reaction> findAll();

    default Optional<Reaction> get(Long id){
        return findById(id);
    }

    default Optional<Reaction> create(Reaction input){
        return Optional.of(save(input));
    }

    default Optional<Reaction> update(Reaction input){
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
