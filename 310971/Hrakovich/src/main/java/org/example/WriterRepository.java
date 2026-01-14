package org.example;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriterRepository extends JpaRepository<Writer, Long> {

    boolean existsByLogin(String login);

    Page<Writer> findByFirstnameContainingIgnoreCase(
            String firstname,
            Pageable pageable
    );

    Page<Writer> findByLastnameContainingIgnoreCase(
            String lastname,
            Pageable pageable
    );
}