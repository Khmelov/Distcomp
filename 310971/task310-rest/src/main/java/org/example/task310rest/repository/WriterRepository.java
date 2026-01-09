package org.example.task310rest.repository;

import org.example.task310rest.model.Writer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WriterRepository extends InMemoryCrudRepository<Writer> {
    public WriterRepository() {
        super(Writer::getId, Writer::setId);
    }

    public Optional<Writer> findByLogin(String login) {
        return findAll().stream()
                .filter(writer -> login.equals(writer.getLogin()))
                .findFirst();
    }
}
