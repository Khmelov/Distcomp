package com.aitor.publisher.repository;

import com.aitor.publisher.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
