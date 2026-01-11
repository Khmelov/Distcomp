package com.distcomp.publisher.writer.repo;

import com.distcomp.publisher.writer.domain.Writer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriterRepository extends JpaRepository<Writer, Long> {
}
