package com.distcomp.data.repository.topic;

import com.distcomp.model.topic.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TopicReactiveRepository extends R2dbcRepository<Topic, Long> {
    Flux<Topic> findByUserWhoPostTopicId(Long userId, Pageable pageable);

    Flux<Topic> findAllBy(Pageable pageable);


    @Modifying
    @Query("DELETE FROM tbl_topic WHERE id = :id")
    Mono<Integer> deleteTopicById(@Param("id") Long id);


    Mono<Topic> findByTitle(String title);
}
