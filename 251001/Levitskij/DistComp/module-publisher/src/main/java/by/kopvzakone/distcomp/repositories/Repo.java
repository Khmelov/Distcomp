package by.kopvzakone.distcomp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@NoRepositoryBean
public interface Repo<T> extends JpaRepository<T, Long> {
    default Stream<T> getAll(){
        return findAll().stream();
    }
    default Optional<T> get(long id){
        return findById(id);
    }
    default Optional<T> create(T input){
        return Optional.of(save(input));
    }
    default Optional<T> update(T input){
        return Optional.of(save(input));
    }
    default void delete(long id){
        if(findById(id).isPresent()) {
            deleteById(id);
        } else {
            throw new NoSuchElementException("No element with id " + id);
        }

    }
}
