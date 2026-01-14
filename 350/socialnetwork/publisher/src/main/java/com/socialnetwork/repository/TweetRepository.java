package com.socialnetwork.repository;

import com.socialnetwork.model.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    Page<Tweet> findAll(Pageable pageable);

    List<Tweet> findByUserId(Long userId);

    boolean existsByTitle(String title);

    @Query("SELECT t FROM Tweet t WHERE t.user.id = :userId")
    Page<Tweet> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Tweet t JOIN t.labels label WHERE label.id = :labelId")
    List<Tweet> findByLabelId(@Param("labelId") Long labelId);

    @Query("SELECT t FROM Tweet t JOIN t.labels label WHERE label.id = :labelId")
    Page<Tweet> findByLabelId(@Param("labelId") Long labelId, Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM Tweet t WHERE t.id = :id")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT COUNT(t) > 0 FROM Tweet t WHERE t.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}