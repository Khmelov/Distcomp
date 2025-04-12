package org.ex.distributed_computing.repository;

import java.util.List;
import org.ex.distributed_computing.model.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {

  List<Author> findAll();

  boolean existsByLogin(String login);
}
