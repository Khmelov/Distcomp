package com.example.demo.repository;

import com.example.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByIssueId(Long issueId);
    boolean existsByContent(String content);
    boolean existsByContentAndIdNot(String content, Long id);
}
