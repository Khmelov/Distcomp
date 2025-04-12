package com.example.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.stream.Stream;

@NoRepositoryBean
public interface Repository<T> extends JpaRepository<T, Long> {
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
    default boolean delete(long id){
        if(existsById(id)){
            deleteById(id);
            return true;
        }else {
            return false;
        }
    }
}
