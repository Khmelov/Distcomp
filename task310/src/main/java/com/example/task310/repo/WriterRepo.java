package com.example.task310.repo;

import com.example.task310.domain.Writer;
import org.springframework.stereotype.Repository;

@Repository
public class WriterRepo extends InMemoryRepo<Writer> {

    @Override
    protected Writer withId(Writer w, long id) {
        return new Writer(
                id,
                w.login(),
                w.password(),
                w.firstname(),
                w.lastname()
        );
    }
}
