package com.task.rest.repository;

import com.task.rest.model.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long>{

    boolean existsByLogin(String login);

}
