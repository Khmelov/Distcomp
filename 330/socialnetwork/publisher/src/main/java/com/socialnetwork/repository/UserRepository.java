package com.socialnetwork.repository;

import com.socialnetwork.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :id")
    boolean existsById(@Param("id") Long id);
}