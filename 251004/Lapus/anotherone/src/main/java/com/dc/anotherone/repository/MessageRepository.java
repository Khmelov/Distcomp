package com.dc.anotherone.repository;

import com.dc.anotherone.model.blo.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAll(Pageable pageable);
}
