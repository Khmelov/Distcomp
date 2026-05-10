package com.writerservice.repositories;

import com.writerservice.dtos.WriterResponseTo;
import com.writerservice.models.Writer;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface WriterRepository extends JpaRepository<Writer, Long> {
    Optional<Writer> findByLogin(String login);

    boolean existsByLoginAndIdNot(String login, Long id);

    Optional<Writer> deleteWriterById(Long id);
}
