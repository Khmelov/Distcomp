package com.sergey.orsik.repository;

import com.sergey.orsik.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long>, JpaSpecificationExecutor<Tweet> {
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Long id);

    @Query("SELECT DISTINCT t FROM Tweet t LEFT JOIN FETCH t.labels WHERE t.id = :id")
    Optional<Tweet> findByIdWithLabels(@Param("id") Long id);
}
