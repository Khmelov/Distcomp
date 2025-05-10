package com.example.restservice.repository;

import com.example.restservice.model.Message;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    //List<Message> getAll();
    Message getMessageById(@Positive Long id);
    void deleteMessageById(@Positive Long id);
}
