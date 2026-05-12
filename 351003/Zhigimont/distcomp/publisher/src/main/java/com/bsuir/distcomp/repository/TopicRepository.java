package com.bsuir.distcomp.repository;

import com.bsuir.distcomp.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByTitle(String title);

    void deleteAllByWriterId(Long writerId);
}
