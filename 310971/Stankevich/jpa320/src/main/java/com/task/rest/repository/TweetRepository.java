package com.task.rest.repository;

import com.task.rest.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long>, JpaSpecificationExecutor<Tweet> {

    List<Tweet> findByAuthorId(Long authorId);

    boolean existsByTitle(String title);

    Optional<Tweet> findByTitle(String title);

    @Query("SELECT COUNT(t) FROM Tweet t JOIN t.marks m WHERE m.id = :markId")
    long countByMarksId(@Param("markId") Long markId);
}
