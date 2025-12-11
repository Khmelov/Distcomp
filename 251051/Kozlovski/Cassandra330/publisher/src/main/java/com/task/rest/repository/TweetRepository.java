package com.task.rest.repository;

import com.task.rest.model.Mark;
import com.task.rest.model.Tweet;
import com.task.rest.model.Writer;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long>{

    List<Tweet> findByMarks(Mark Id);

    Optional<Tweet> findByWriterId(Long Id);

    boolean existsByWriterId(Long Id);

    @Query("SELECT COUNT(t) > 0 FROM Tweet t JOIN t.marks m WHERE m.id = :markId")
    boolean existsByMarkId(@Param("markId") Long markId);
}
