package com.distcomp.data.repository.tag;

import com.distcomp.model.tag.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TagReactiveRepository extends R2dbcRepository<Tag, Long> {
    Mono<Tag> findByName(String name);

    Flux<Tag> findAllBy(Pageable pageable);

    @Modifying
    @Query("DELETE FROM tags WHERE id = :id")
    Mono<Integer> deleteTagById(@Param("id") Long id);
}
