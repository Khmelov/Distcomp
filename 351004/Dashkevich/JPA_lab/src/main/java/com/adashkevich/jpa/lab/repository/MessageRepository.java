package com.adashkevich.jpa.lab.repository;

import com.adashkevich.jpa.lab.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByNews_IdOrderByIdAsc(Long newsId);
    void deleteByNews_Id(Long newsId);
}
