package com.example.demo.repository;

import com.example.demo.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByAuthorId(Long authorId);
    List<Issue> findAllByMarksId(Long markId);
    boolean existsByTitle(String title);
    @Query("SELECT COUNT(i) FROM Issue i JOIN i.marks m WHERE m.id = :markId")
    long countIssuesByMarkId(@Param("markId") Long markId);
}
