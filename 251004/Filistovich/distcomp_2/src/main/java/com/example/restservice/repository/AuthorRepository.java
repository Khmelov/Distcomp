package com.example.restservice.repository;

import com.example.restservice.model.Author;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    //List<Author> getAll();

    Author getAuthorById(@Positive Long id);

    void deleteAuthorById(@Positive Long id);

    boolean existsByLogin(@Size(min=2, max=64) String login);
}
