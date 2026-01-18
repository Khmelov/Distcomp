package com.rest.restapp.repository;

import com.rest.restapp.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    boolean existsByTitle(String title);
}
