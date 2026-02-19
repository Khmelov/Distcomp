package com.distcomp.data.repository.m2m;

import com.distcomp.model.m2m.TopicTag;
import com.distcomp.model.m2m.TopicTagId;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TopicTagReactiveRepository extends ReactiveCrudRepository<TopicTag, TopicTagId> {

    @Query("SELECT * FROM tbl_topic_tag WHERE topic_id = :topicId")
    Flux<TopicTag> findByTopicId(Long topicId);

    @Query("DELETE FROM tbl_topic_tag WHERE topic_id = :topicId")
    Mono<Void> deleteByTopicId(Long topicId);
}