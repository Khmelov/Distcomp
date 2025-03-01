package com.dc.anotherone.repository;

import com.dc.anotherone.model.blo.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByLogin(String login);
}
