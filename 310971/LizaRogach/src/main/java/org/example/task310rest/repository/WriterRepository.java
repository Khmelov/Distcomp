package org.example.task310rest.repository;

import org.example.task310rest.model.Writer;
import org.springframework.stereotype.Repository;

@Repository
public class WriterRepository extends InMemoryCrudRepository<Writer> {
    public WriterRepository() {
        super(Writer::getId, Writer::setId);
    }
}


