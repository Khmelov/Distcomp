package com.publick.repository;

import com.publick.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends CrudRepository<Issue, Long> {

    List<Issue> findByAuthorId(Long authorId);

    Optional<Issue> findByTitleIgnoreCase(String title);
}