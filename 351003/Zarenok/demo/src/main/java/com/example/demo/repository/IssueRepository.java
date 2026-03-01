package com.example.demo.repository;

import com.example.demo.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByAuthorId(Long authorId);
    List<Issue> findAllByMarksId(Long markId);
}
