package com.task310.blogplatform.repository;

import com.task310.blogplatform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);
    
    Page<User> findAll(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.login LIKE %:login%")
    Page<User> findByLoginContaining(@Param("login") String login, Pageable pageable);
}

