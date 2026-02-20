package com.distcomp.data.repository.user;

import com.distcomp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserReactiveRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByLogin(String login);

    Flux<User> findAllBy(Pageable pageable);

    @Modifying
    @Query("DELETE FROM tbl_user WHERE id = :id")
    Mono<Integer> deleteUserById(@Param("id") Long id);

    Mono<Boolean> existsByLogin(String login);
}