package com.blog.repository;

import com.blog.entity.Writer;
import org.springframework.stereotype.Repository;

@Repository
public class WriterRepositoryImpl extends InMemoryGenericRepository<Writer> implements WriterRepository {
    // Implementation specific to Writer can be added here if needed
}