package com.bsuir.romanmuhtasarov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bsuir.romanmuhtasarov.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
